package common;

public enum OrderStatus {
    PLAYING(1,"待支付"),OK(2,"支付完成");
    private int flag;
    private String desc;
    OrderStatus(int flag,String desc) {
        this.flag = flag;
        this.desc = desc;
    }

    //OrderStatus.values()将枚举里面的值变为数组
    public static OrderStatus valueOf (int flag) {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.flag == flag) {
                return orderStatus;
            }
        }
        throw new RuntimeException("OrderStatus is not found");
    }

    public int getFlag() {
        return flag;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "OrderStatus{" +
                "flag=" + flag +
                ", desc='" + desc + '\'' +
                '}';
    }
}
