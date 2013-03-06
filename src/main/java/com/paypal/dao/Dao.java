package com.paypal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.paypal.api.payments.Payment;
import com.paypal.core.LoggingManager;
import com.paypal.model.User;
import com.paypal.model.UserPaymentDetail;
import com.paypal.util.AppConstants;
import com.paypal.util.EncryptionUtil;

/**
 * <code>Dao</code> handles Data Access Operations for our sample application
 * This is just for reference purpose. Database transactions and roll-backs are
 * not handled to keep the code simple, HSQL DBs auto-commit=true feature is used 
 * for database operations. Real world applications should have
 * a robust implementation for the DAO layer that caters to their individual 
 * complex needs.
 * @author tkanta
 * 
 */
public class Dao {

	/**
	 * Insert User into the database.
	 * 
	 * @param email
	 *            Email
	 * @param password
	 *            Password
	 * @param id
	 *            Id
	 * @throws SQLException
	 */
	public static void insertUser(String email, String password, String id)
			throws SQLException {
		Connection con = DBConnection.getConnection();
		try {
			PreparedStatement pst = con
					.prepareStatement("INSERT INTO ppusers(email,password,creditcard_id) VALUES(?,?,?)");
			pst.clearParameters();
			pst.setString(1, email);
			pst.setString(2, EncryptionUtil.encrypt(password));
			pst.setString(3, id);
			pst.executeUpdate();
		} catch (SQLException sqlex) {
			LoggingManager.debug(Dao.class, sqlex.getMessage());
			throw sqlex;
		}

	}

	/**
	 * Update user record
	 * 
	 * @param user
	 *            User object
	 * @throws SQLException
	 */
	public static void updateUser(User user) throws SQLException {
		Connection con = DBConnection.getConnection();
		PreparedStatement pst = null;
		try {
			if (user.getPassword() != null
					&& user.getPassword() != AppConstants.EMPTY_STRING) {
				pst = con
						.prepareStatement("UPDATE ppusers SET creditcard_id=? , password=? WHERE email=?");
				pst.setString(1, user.getCreditCardId());
				pst.setString(2, EncryptionUtil.encrypt(user.getPassword()));
				pst.setString(3, user.getEmail());
			} else {
				pst = con
						.prepareStatement("UPDATE ppusers SET creditcard_id=? WHERE email=?");
				pst.setString(1, user.getCreditCardId());
				pst.setString(2, user.getEmail());
			}
			pst.executeUpdate();
		} catch (SQLException sqlex) {
			LoggingManager.debug(Dao.class, sqlex.getMessage());
			throw sqlex;
		}

	}

	/**
	 * Retrieves order for the specified user
	 * 
	 * @param userid
	 *            User id
	 * @return List of orders on the user
	 * @throws SQLException
	 */
	public static List<UserPaymentDetail> getOrdersByUserId(String userid)
			throws SQLException {
		Connection con = DBConnection.getConnection();
		List<UserPaymentDetail> paymentList = null;
		try {
			PreparedStatement pst = con
					.prepareStatement("SELECT * FROM orders WHERE user_id=?");
			pst.setString(1, userid);
			ResultSet rs = pst.executeQuery();
			paymentList = new ArrayList<UserPaymentDetail>();
			while (rs.next()) {
				UserPaymentDetail paymentDetail = new UserPaymentDetail();
				paymentDetail.setPaymentId(rs.getString(3));
				paymentDetail.setPaymentAmount(rs.getString(4));
				paymentDetail.setPaymentStatus(rs.getString(5));
				paymentDetail.setPaymentDate(rs.getString(6));
				paymentDetail.setPaymentdescription(rs.getString(7));
				paymentList.add(paymentDetail);
			}

		} catch (SQLException sqlex) {
			LoggingManager.debug(Dao.class, sqlex.getMessage());
			throw sqlex;
		}

		return paymentList;
	}

	/**
	 * Retrieves the UserPaymentDetail for the corresponding orderId
	 * 
	 * @param orderId
	 *            Order id
	 * @return UserPaymentDetail object
	 * @throws SQLException
	 */
	public static UserPaymentDetail getOrderByOrderId(String orderId)
			throws SQLException {
		Connection con = DBConnection.getConnection();
		UserPaymentDetail userPaymentDetail = null;
		try {
			PreparedStatement pst = con
					.prepareStatement("SELECT * FROM orders WHERE order_id=?");
			pst.setString(1, orderId);
			ResultSet rs = pst.executeQuery();
			userPaymentDetail = new UserPaymentDetail();
			while (rs.next()) {
				userPaymentDetail.setOrderId(String.valueOf(rs.getInt(1)));
				userPaymentDetail.setUserId(String.valueOf(rs.getInt(2)));
				userPaymentDetail.setPaymentId(rs.getString(3));
				userPaymentDetail.setPaymentAmount(rs.getString(4));
				userPaymentDetail.setPaymentStatus(rs.getString(5));
				userPaymentDetail.setPaymentDate(rs.getString(6));
				userPaymentDetail.setPaymentdescription(rs.getString(7));
			}
		} catch (SQLException sqlex) {
			LoggingManager.debug(Dao.class, sqlex.getMessage());
			throw sqlex;
		}

		return userPaymentDetail;

	}

	/**
	 * Creates an order for the User
	 * 
	 * @param userId
	 *            User Id
	 * @return Order Id
	 * @throws SQLException
	 */

	public static String createOrder(String userId) throws SQLException {
		Connection con = DBConnection.getConnection();
		String currentOrderId = null;
		try {
			PreparedStatement pst = con
					.prepareStatement("INSERT INTO orders (user_id) VALUES(?)");
			pst.setString(1, userId);
			pst.executeUpdate();
			// return current order id
			pst = con
					.prepareStatement("SELECT TOP 1 order_id FROM orders WHERE user_id=? ORDER BY order_id DESC");
			pst.setString(1, userId);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				currentOrderId = rs.getString(1);
			}
		} catch (SQLException sqlex) {
			LoggingManager.debug(Dao.class, sqlex.getMessage());
			throw sqlex;
		}
		return currentOrderId;
	}

	/**
	 * Inserts payment order
	 * 
	 * @param orderid
	 *            Order Id
	 * @param userid
	 *            User Id
	 * @param payment
	 *            Payment object
	 * @throws SQLException
	 */
	public static void insertOrder(String orderid, String userid,
			Payment payment) throws SQLException {
		Connection con = DBConnection.getConnection();

		try {
			PreparedStatement pst = con
					.prepareStatement("UPDATE orders SET payment_id=? , amount=? , state=? , date=? , description=? WHERE order_id=? AND user_id=?");
			pst.setString(1, payment.getId());
			pst.setString(2, payment.getTransactions().get(0).getAmount()
					.getTotal());
			pst.setString(3, payment.getState());
			pst.setString(4, payment.getCreateTime());
			pst.setString(5, payment.getTransactions().get(0).getDescription());
			pst.setString(6, orderid);
			pst.setString(7, userid);
			pst.executeUpdate();
		} catch (SQLException sqlex) {
			LoggingManager.debug(Dao.class, sqlex.getMessage());
			throw sqlex;
		}
	}

	/**
	 * Update order status
	 * 
	 * @param orderId
	 *            Order Id
	 * @param paymentstatus
	 *            Payment status to be update
	 * @throws SQLException
	 */
	public static void updateOrderStatus(String orderId, String paymentstatus)
			throws SQLException {
		Connection con = DBConnection.getConnection();

		try {
			PreparedStatement pst = con
					.prepareStatement("UPDATE orders SET state=? WHERE order_id=?");
			pst.setString(1, paymentstatus);
			pst.setString(2, orderId);
			pst.executeUpdate();
		} catch (SQLException sqlex) {
			LoggingManager.debug(Dao.class, sqlex.getMessage());
			throw sqlex;
		}
	}

	/**
	 * Retrieves User using the email
	 * 
	 * @param email
	 *            Email of the User
	 * @return LoggedInUser object
	 * @throws SQLException
	 */
	public static User getUser(String email) throws SQLException {
		User user = null;
		Connection con = DBConnection.getConnection();
		try {
			PreparedStatement pst = con
					.prepareStatement("SELECT * FROM ppusers WHERE email=?");
			pst.clearParameters();
			pst.setString(1, email);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				user = new User();
				user.setId(rs.getString(1));
				user.setEmail(rs.getString(2));
				user.setPassword(EncryptionUtil.decrypt(rs.getString(3)));
				user.setCreditCardId(rs.getString(4));
			}
		} catch (SQLException sqlex) {
			LoggingManager.debug(Dao.class, sqlex.getMessage());
			throw sqlex;
		}
		return user;
	}

}
