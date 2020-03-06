package servlet;

import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InboundServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset = UTF-8");
        resp.setCharacterEncoding("UTF-8");


        String name = req.getParameter("name");
        String introduce = req.getParameter("introduce");
        Integer stock = Integer.parseInt(req.getParameter("stock"));
        String unit = req.getParameter("unit");
        Integer price = Integer.parseInt(req.getParameter("price"));
        Integer discount = Integer.parseInt(req.getParameter("discount"));


        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String sql = "insert into goods(name,introduce,stock,unit,price,discount) " +
                    "values(?,?,?,?,?,?)";
            connection = DBUtil.getConnection(true);
            preparedStatement = connection.prepareStatement(sql);


            preparedStatement.setString(1,name);
            preparedStatement.setString(2,introduce);
            preparedStatement.setInt(3,stock);
            preparedStatement.setString(4,unit);
            preparedStatement.setInt(5,price);
            preparedStatement.setInt(6,discount);

            preparedStatement.executeUpdate();

            //跳转到登录界面
            resp.sendRedirect("index.html");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection,preparedStatement,null);
        }

    }
}
