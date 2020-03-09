package servlet;
import common.OrderStatus;
import entity.Goods;
import entity.Order;
import entity.OrderItem;
import util.DBUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/buyGoodsServlet")
public class buyGoodsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //System.out.println("buyGoodsServlet - doGet");
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //System.out.println("buyGoodsServlet - doPost");
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession();
        Order order = (Order)session.getAttribute("order");
        //System.out.println(order);
        List<Goods> goodsList = (List<Goods>)session.getAttribute("goodsList");
        //System.out.println(goodsList);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        order.setFinish_time(LocalDateTime.now().format(formatter));

        order.setOrder_status(OrderStatus.OK);

        //提交订单
        boolean effect = this.commitOrder(order);

        //当插入数据库为真，表示已经购买了
        //遍历所有货物，将货物库存进行修改
        if(effect) {
            for (Goods goods : goodsList) {
                boolean isUpdate = this.updateAfterBuy(goods,goods.getBuyGoodsNum());
                if (isUpdate) {
                    System.out.println("更新库存成功");
                } else {
                    System.out.println("更新库存失败");
                }
            }
            resp.sendRedirect("buyGoodsSuccess.html");
        }
    }

    //更新goods这张表
    private boolean updateAfterBuy(Goods goods, int buyGoodsNum) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        boolean effect = false;
        try {
            String sql = "update goods set stock=? where id=?";
            connection = DBUtil.getConnection(true);
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1,goods.getStock() - goods.getBuyGoodsNum());
            preparedStatement.setInt(2,goods.getId());

            if (preparedStatement.executeUpdate() == 1) {
                effect = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return effect;
    }

    private boolean commitOrder(Order order) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            String insertOrder = "insert into `order`(id,account_id,create_time,finish_time,actual_amount," +
                    "total_money,order_status,account_name) values (?,?,now(),now(),?,?,?,?)";
            String insertOrderItem = "insert into order_item(order_id,goods_id,goods_name,goods_introduce," +
                    "goods_num,goods_unit,goods_price,goods_discount) values (?,?,?,?,?,?,?,?)";
            connection = DBUtil.getConnection(false);//可能进行回滚，需要手动提交

            preparedStatement = connection.prepareStatement(insertOrder);
            preparedStatement.setString(1,order.getId());
            preparedStatement.setInt(2,order.getAccount_id());
            preparedStatement.setInt(3,order.getActual_amountInt());
            preparedStatement.setInt(4,order.getTotal_moneyInt());
            preparedStatement.setInt(5,order.getOrder_statusDesc().getFlag());
            preparedStatement.setString(6,order.getAccount_name());

            if (preparedStatement.executeUpdate() == 0) {
                throw new RuntimeException("插入失败");
            }

            //插入成功,开始插入订单项
            preparedStatement = connection.prepareStatement(insertOrderItem);
            //批量进行插入
            for (OrderItem orderItem : order.orderItemList) {
                preparedStatement.setString(1,order.getId());
                preparedStatement.setInt(2,orderItem.getGoods_id());
                preparedStatement.setString(3,orderItem.getGoods_name());
                preparedStatement.setString(4,orderItem.getGoods_introduce());
                preparedStatement.setInt(5,orderItem.getGoods_num());
                preparedStatement.setString(6,orderItem.getGoods_unit());
                preparedStatement.setInt(7,orderItem.getIntGoods_Price());
                preparedStatement.setInt(8,orderItem.getGoods_discount());
                //将每一项preparedStatement缓存
                preparedStatement.addBatch();
            }
            int[] effects = preparedStatement.executeBatch();//批量插入
            for (int i : effects) {
                if (i == 0) {
                    throw  new RuntimeException("插入订单项失败");
                }
            }
            //手动进行提交
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            return false;
        } finally {
            DBUtil.close(connection,preparedStatement,null);
        }
        return true;
    }
}
