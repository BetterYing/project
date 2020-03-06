//浏览订单
package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.OrderStatus;
import entity.Account;
import entity.Order;
import entity.OrderItem;
import util.DBUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/browseOrder")
public class BrowseOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset = UTF-8");
        resp.setCharacterEncoding("UTF-8");

        //根据当前的用户Id，进行订单查找
        HttpSession session = req.getSession();
        Account account = (Account) session.getAttribute("user");
        List<Order> list = this.queryOrder(account.getId());//查询结果可能是多个订单。用List<Order>保存

        if (list == null) {
            //判断查询结果，如果是空，说明没有订单
            System.out.println("订单链表为空");
        } else {
            //如果不为空，那么将list转为json，发送给前端
            ObjectMapper mapper = new ObjectMapper();
            PrintWriter pw = resp.getWriter();//将响应包推给浏览器
            mapper.writeValue(pw,list);//将list转换为字符串，并将json字符串填充到PrintWriter当中
            Writer writer = resp.getWriter();
            writer.write(pw.toString());//把流响应给前端页面
        }
    }

    private List<Order> queryOrder(int accountId) {
        List<Order> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            String sql = this.getSql("@query_Order_by_account");
            connection = DBUtil.getConnection(true);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,accountId);
            resultSet = preparedStatement.executeQuery();

            Order order = null;
            while (resultSet.next()) {
                //1、订单需要解析
                if(order == null) {
                    order = new Order();
                    this.extractOrder(order,resultSet);
                    list.add(order);
                }

                String orderId = resultSet.getString("order_id");
                if(!orderId.equals(order.getId())) {
                    //不同的订单
                    order = new Order();
                    this.extractOrder(order,resultSet);
                    list.add(order);
                }
                /*Order order = new Order();//订单的对象
                this.extractOrder(order,resultSet);
                list.add(order);*/
                //2、订单项需要解析
                OrderItem orderItem = this.extractOrderItem(resultSet);
                order.orderItemList.add(orderItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection,preparedStatement,resultSet);
        }
        return list;
    }

    private OrderItem extractOrderItem(ResultSet resultSet) throws SQLException {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(resultSet.getInt("item_id"));
        orderItem.setGoods_id(resultSet.getInt("goods_id"));
        orderItem.setGoods_name(resultSet.getString("goods_name"));
        orderItem.setGoods_introduce(resultSet.getString("goods_introduce"));
        orderItem.setGoods_num(resultSet.getInt("goods_num"));
        orderItem.setGoods_unit(resultSet.getString("goods_unit"));
        orderItem.setGoods_price(resultSet.getInt("goods_price"));
        orderItem.setGoods_discount(resultSet.getInt("goods_discount"));
        return orderItem;
    }

    private void extractOrder(Order order, ResultSet resultSet) throws SQLException {
        order.setId(resultSet.getString("order_id"));
        order.setAccount_id(resultSet.getInt("account_id"));
        order.setAccount_name(resultSet.getString("account_name"));
        order.setCreate_time(resultSet.getString("create_time"));
        order.setFinish_time(resultSet.getString("finish_time"));
        order.setActual_amount(resultSet.getInt("actual_amount"));
        order.setTotal_money(resultSet.getInt("total_money"));
        order.setOrder_status(OrderStatus.valueOf(resultSet.getInt("order_status")));
    }

    /**
     * 生成一条sql语句， 用I/O流
     * @param sqlName
     * @return
     */
    private String getSql(String sqlName) {
        //getClass()获得当前的class对象
        //getClassLoader()获取类加载器
        InputStream in = this.getClass().getClassLoader().
                getResourceAsStream("script/" + sqlName.substring(1) + ".sql");
        if(in == null) {
            throw new RuntimeException("加载sql文件出错");
        } else {
            //字节流转换为字符流
            InputStreamReader isr = new InputStreamReader(in);

            BufferedReader reader = new BufferedReader(isr);
            try {
                StringBuilder sb = new StringBuilder();
                sb.append(reader.readLine());

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(" ").append(line);
                }
                //System.out.println(sb);
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("转化sql语句发生异常");
            }
        }
    }
}
