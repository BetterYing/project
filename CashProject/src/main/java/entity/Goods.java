package entity;

public class Goods {
    private Integer id;
    private String name;
    private String introduce;
    private Integer stock;
    private String unit;
    private Integer price;//存入数据库为整数
    private Integer discount;

    private Integer buyGoodsNum;//记录需要购买当前商品的数量

    public Integer getBuyGoodsNum() {
        return buyGoodsNum;
    }


    public void setBuyGoodsNum(Integer buyGoodsNum) {
        this.buyGoodsNum = buyGoodsNum;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


    public double getPrice() {
        return price * 1.0 /100;
    }
    public int getPriceInt() {
        return price;
    }


    public void setPrice(Integer price) {
        this.price = price;
    }



    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }


//    @Override
//    public String toString() {
//        return "Goods{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
//                ", introduce='" + introduce + '\'' +
//                ", stock=" + stock +
//                ", unit='" + unit + '\'' +
//                ", price=" + price +
//                ", discount=" + discount +
//                ", buyGoodsNum=" + buyGoodsNum +
//                '}';
//    }
}
