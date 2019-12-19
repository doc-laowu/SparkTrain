//package RealTimeDial;
//
//import org.apache.commons.lang.StringUtils;
//import org.apache.commons.lang.Validate;
//import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
//import org.apache.rocketmq.client.consumer.MQPushConsumer;
//import org.apache.rocketmq.client.consumer.listener.*;
//import org.apache.rocketmq.client.exception.MQClientException;
//import org.apache.rocketmq.common.message.MessageExt;
//import org.apache.spark.storage.StorageLevel;
//import org.apache.spark.streaming.receiver.Receiver;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.util.HashMap;
//import java.util.List;
//
///**
// * RocketMQ Receiver
// * @author mengyao
// *
// */
//public class RocketMQReceiver extends Receiver<BCD> {
//
//    /**
//     *
//     */
//    private static final long serialVersionUID = 2274826339951693341L;
//    private MQPushConsumer consumer;
//    private boolean ordered;
//    private HashMap<String, String> conf;
//
//
//    public RocketMQReceiver(HashMap<String, String> conf, StorageLevel storageLevel) {
//        super(storageLevel);
//        this.conf = conf;
//    }
//
//    @Override
//    public void onStart() {
//        Validate.notEmpty(conf, "Consumer properties can not be empty");
//        ordered = RocketMQConfig.getBoolean(conf, RocketMQConfig.CONSUMER_MESSAGES_ORDERLY, true);
//        consumer = new DefaultMQPushConsumer();
//        RocketMQConfig.buildConsumerConfigs(conf, (DefaultMQPushConsumer)consumer);
//        if (ordered) {
//            consumer.registerMessageListener(new MessageListenerOrderly() {
//                @Override
//                public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
//                    if (process(msgs)) {
//                        return ConsumeOrderlyStatus.SUCCESS;
//                    } else {
//                        return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
//                    }
//                }
//            });
//        } else {
//            consumer.registerMessageListener(new MessageListenerConcurrently() {
//                @Override
//                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
//                    if (process(msgs)) {
//                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//                    } else {
//                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
//                    }
//                }
//            });
//        }
//        try {
//            consumer.start();
//            System.out.println("==== RocketMQReceiver start ====");
//        } catch (MQClientException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//    }
//
//    public boolean process(List<MessageExt> msgs) {
//        if (msgs.isEmpty()) {
//            System.out.println("==== Msgs is null! ====");
//            return true;
//        }
//        System.out.println("==== receiver msgs: "+msgs.size()+" record. ====");
//        try {
//            for (MessageExt messageExt : msgs) {
//                //MQBillMessage message = SerializationUtils.deserialize(messageExt.getBody());
//                MQBillMessage message = deserialize(messageExt.getBody());
//                if (null != message) {
//                    BillInfoFmt billFmt = ConvertTool.convertGagBillToOdsBillFmt(message.getData());
//                    if (validBillType(billFmt)) {
//                            /**
//　　　　　　　　　　　　　　　 * this.store(BCD) 简单的接收一条存储一条，缺点是没有确认机制，不具备容错保证，会出现数据丢失。优点则是效率更高。
//　　　　　　　　　　　　　　　 * this.store(Iterator<BCD>)阻塞调用，当接收到的记录都存储到Spark后才会确认成功，当接收方采用复制（默认存储级别为复制）则在复制完成后确认成功，但在缓冲中的数据不被保证，会被重新发送。具备容错保证，可确保数据0丢失。
//　　　　　　　　　　　　　　　 */
//                        this.store(new BCD(billFmt.getId(), billFmt.getReceivableAmount(), billFmt.getSaleTime(), billFmt.getBillType(), billFmt.getShopId(), billFmt.getShopEntityId()));
//                    }
//                    billFmt=null;
//                    message.setData(null);
//                    message = null;
//                } else {
//                    System.out.println("==== receiver msg is:"+messageExt.getBody()+", deserialize faild. ====");
//                }
//            }
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    /**
//     * 验证账单类型是否为1、3、6
//     * @param bean
//     * @return
//     */
//    static boolean validBillType(BillInfoFmt bean) {
//        if (null == bean) {
//            System.out.println("==== BillBean is null! ====");
//            return false;
//        }
//        String billType = bean.getBillType();
//        if (StringUtils.isEmpty(billType)) {
//            System.out.println("==== BillBean.billType is null! ====");
//            return false;
//        }
//        String billTypeTrim = billType.trim();
//        return billTypeTrim.equals("1") || billType.equals("3") || billType.equals("6");
//    }
//
//    @Override
//    public void onStop() {
//        consumer.shutdown();
//        System.out.println("==== RocketMQReceiver stop ====");
//    }
//
//    private static MQBillMessage deserialize(byte[] body) throws Exception {
//        if (body == null) {
//            throw new IllegalArgumentException("The byte array must not be null");
//        }
//        MQBillMessage message = null;
//        ObjectInputStream in = null;
//        ByteArrayInputStream bais = null;
//        try {
//            bais = new ByteArrayInputStream(body);
//            in = new ObjectInputStream(bais);
//            message = (MQBillMessage) in.readObject();
//        } catch (final ClassNotFoundException ex) {
//            ex.printStackTrace();
//            throw ex;
//        } catch (final IOException ex) {
//            ex.printStackTrace();
//            throw ex;
//        } finally {
//            try {
//                if (bais != null) {
//                    bais.close();
//                }
//                if (in != null) {
//                    in.close();
//                }
//            } catch (final IOException ex) {
//                // ignore close exception
//            }
//        }
//        return message;
//    }
//
//}