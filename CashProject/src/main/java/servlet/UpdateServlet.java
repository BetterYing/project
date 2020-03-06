/**
 * 更新商品
 */
package servlet;

import entity.Goods;
import util.DBUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet("/updateGoods")
public class UpdateServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //System.out.println("===========更新=============");
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String goodIdString = req.getParameter("goodsID");
        int goodIdInt = Integer.valueOf(goodIdString);
        String name = req.getParameter("name");
        String introduce = req.getParameter("introduce");
        String stock =  req.getParameter("stock");
        String unit = req.getParameter("unit");
        String price =  req.getParameter("price");
        Double doublePrice = Double.valueOf(price);//12.5
        int realPrice = new Double(doublePrice*100).intValue(); // double类型转int
        String discount =  req.getParameter("discount");
        //System.out.println(discount);

        // 查看当前goods是否存在
        Goods goods = this.getGoods(goodIdInt);
        if(goods == null) {
            System.out.println("没有改商品");
            resp.sendRedirect("index.html");
        } else {
            // 检查完成之后如果存在进行删除
            goods.setName(name);
            goods.setIntroduce(introduce);
            goods.setStock(Integer.valueOf(stock));
            goods.setUnit(unit);
            goods.setPrice(realPrice);
            goods.setDiscount(Integer.valueOf(discount));

            //把查询的商品进行更新，随后对数据库进行操作，把当前的goods更新
            boolean effect = this.modifyGoods(goods);
            if(effect) {
                System.out.println("更新成功");
                resp.sendRedirect("goodsbrowse.html");
            } else {
                System.out.println("更新失败");
                resp.sendRedirect("index.html");
            }
        }
    }

    private boolean modifyGoods(Goods goods) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean effect = false;
        try {
            String sql = "update goods set name=?,introduce=?,stock=?,unit=?,price=?,discount=? where id=?";
            connection = DBUtil.getConnection(true);
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1,goods.getName());
            preparedStatement.setString(2,goods.getIntroduce());
            preparedStatement.setInt(3,goods.getStock());
            preparedStatement.setString(4,goods.getUnit());
            preparedStatement.setInt(5,goods.getPriceInt());
            preparedStatement.setInt(6,goods.getDiscount());
            preparedStatement.setInt(7,goods.getId());

            effect = (preparedStatement.executeUpdate() == 1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection,preparedStatement,null);
        }
        return effect;
    }

    // 查询当前goods是否存在
    private Goods getGoods(int goodId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Goods goods = null;
        try {
            String sql = "select * from goods where id=?";
            connection = DBUtil.getConnection(true);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,goodId);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                goods = this.extractGoods(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection,preparedStatement,resultSet);
        }
        return goods;
    }

    //解析resultSet当中的商品信息，组装从一个商品的对象goods
    public Goods extractGoods(ResultSet resultSet) throws SQLException{
        Goods goods = new Goods();
        goods.setId(resultSet.getInt("id"));
        goods.setName(resultSet.getString("name"));
        goods.setIntroduce(resultSet.getString("introduce"));
        goods.setStock(resultSet.getInt("stock"));
        goods.setUnit(resultSet.getString("unit"));
        goods.setPrice(resultSet.getInt("price"));
        goods.setDiscount(resultSet.getInt("discount"));
        return goods;
    }
}
