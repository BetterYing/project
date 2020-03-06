/**
 * 浏览商品
 */
package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Goods;
import util.DBUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/browseGoods")
public class GoodsBrowseServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset = UTF-8");
        resp.setCharacterEncoding("UTF-8");

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<Goods> list = new ArrayList<>();

        try {
            String sql = "select id,name,introduce,stock,unit,price,discount" +
                    " from goods";
            connection = DBUtil.getConnection(true);
            preparedStatement = connection.prepareStatement(sql);

            resultSet = preparedStatement.executeQuery();
            //解析结果集里面的内容
            while (resultSet.next()) {
                Goods goods = this.extractGoods(resultSet);
                if (goods != null) {
                    list.add(goods);
                }
            }
            //System.out.println("list " + list);

            //将list转为json，然后发给前端 进行解析
            ObjectMapper mapper = new ObjectMapper();//ObjectMapper方便将模型对象转换为json

            PrintWriter pw = resp.getWriter();//将响应包推给浏览器
            mapper.writeValue(pw,list);//将list转换为json字符串，并将json字符串填充到PrintWriter流当中

            Writer writer = resp.getWriter();

            writer.write(pw.toString());//把流响应给前端页面

        }catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection ,preparedStatement,resultSet);
        }
    }

    public Goods extractGoods (ResultSet resultSet) throws SQLException {
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
