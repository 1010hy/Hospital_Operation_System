package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class PatientDao {
	private static PatientDao instance;

	private PatientDao() {
	}

	public static PatientDao getInstance() {
		if (instance == null) {
			instance = new PatientDao();
		}
		return instance;
	}

	private Connection getConnection() {
		Connection conn = null;
		try {
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/OracleDB");
			conn = ds.getConnection();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}

	// 환자관리
	public List<Patient> selectAll() throws SQLException {

		List<Patient> list = new ArrayList<Patient>();
		String sql = "select * from patient where doctor_no=? ORDER BY patient_no asc";
		String doctor_no = "2";

		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			connection = getConnection();
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, doctor_no);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				do {
					Patient patient = new Patient();

					patient.setAddress(rs.getString("address"));
					patient.setBirth(rs.getString("birth"));
					patient.setContact(rs.getString("contact"));

					patient.setGender(rs.getString("gender"));
					patient.setPatient_name(rs.getString("patient_name"));
					patient.setPatient_no(rs.getInt("patient_no"));
					patient.setProtector_contact(rs.getString("protector_contact"));
					patient.setSocial_number(rs.getString("social_number"));
					list.add(patient);

				} while (rs.next());
			}
		} catch (SQLException e) {

			e.printStackTrace();
			System.out.println("selectAll rs까지 Err: " + e.getMessage());

		} finally {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			if (connection != null)
				connection.close();
		}

		return list;
	}

	public List<Patient> patientSearch(String department, String doctorName, String reservationDate, String patientName)
			throws SQLException {

		List<Patient> list = new ArrayList<Patient>();
		List<Patient> list_result = new ArrayList<Patient>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
//		int i=2;
//		int j=1;
//		String sqlbase			= 						   "SELECT p.* FROM patient p";
//		if (department!="*")		sqlbase=sqlbase.concat(", (select * from doctor where department=?) d"); j++; i=i*3;
//		if (doctorName!="*")		sqlbase=sqlbase.concat(", (select * from doctor where doctor_name=?) dd"); j++; i=i*5;
//		if (reservationDate!="*")	sqlbase=sqlbase.concat(", (select * from reservation where reservation_date=?) r"); j++; i=i*7;
//									sqlbase=sqlbase.concat(" WHERE p.patient_name like ?");
//		if (department!="*")		sqlbase=sqlbase.concat(" AND p.patient_no = r.patient_no");
//		if (doctorName!="*")		sqlbase=sqlbase.concat(" AND p.doctor_no=dd.doctor_no");
//		if (reservationDate!="*")	sqlbase=sqlbase.concat(" AND p.doctor_no=d.doctor_no");
//		System.out.println(sqlbase);
		String sqlbase = "SELECT p.* FROM patient p, (select * from doctor where department like ?) d, "
				+ "(select * from doctor where doctor_name like ?) dd, (select * from reservation where reservation_date like ?) r "
				+ "WHERE p.patient_name like ? AND p.doctor_no=dd.doctor_no AND p.doctor_no=d.doctor_no";
		if (reservationDate != "")
			sqlbase = sqlbase.concat(" AND p.patient_no = r.patient_no");
		System.out.println(sqlbase);
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sqlbase);
			pstmt.setString(1, "%" + department + "%");
			pstmt.setString(2, "%" + doctorName + "%");
			pstmt.setString(3, "%" + reservationDate + "%");
			pstmt.setString(4, "%" + patientName + "%");

			rs = pstmt.executeQuery();
			if (rs.next()) {
				do {
					Patient patient = new Patient();
					patient.setAddress(rs.getString("address"));
					patient.setBirth(rs.getString("Birth"));
					patient.setContact(rs.getString("Contact"));
					patient.setDoctor_no(rs.getString("Doctor_no"));
					patient.setGender(rs.getString("Gender"));
					patient.setPatient_name(rs.getString("Patient_name"));
					patient.setPatient_no(rs.getInt("Patient_no"));
					patient.setProtector_contact(rs.getString("Protector_contact"));
					patient.setSocial_number(rs.getString("Social_number"));
					list.add(patient);
				} while (rs.next());
			}
			list_result.add(list.get(0));
			for (int i = 1; i < list.size(); i++) {
				if (list.get(i).getPatient_no() != list.get(i - 1).getPatient_no()) {
					list_result.add(list.get(i));
				} else
					System.out.println("중복제거");
			}
		} catch (Exception e) {
			System.out.println("check error -> " + e.getMessage());
		} finally {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		}
		return list_result;
	}

	public List<Patient> patientSearch(String patientName) throws SQLException {
		List<Patient> list = new ArrayList<Patient>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select * from patient where patient_name like ?";

		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, "%" + patientName + "%");
			rs = pstmt.executeQuery();
			if (rs.next()) {
				do {
					Patient patient = new Patient();
					patient.setAddress(rs.getString("address"));
					patient.setBirth(rs.getString("birth"));
					patient.setContact(rs.getString("contact"));
					patient.setGender(rs.getString("Gender"));
					patient.setPatient_name(rs.getString("Patient_name"));
					patient.setPatient_no(rs.getInt("Patient_no"));
					patient.setProtector_contact(rs.getString("Protector_contact"));
					patient.setSocial_number(rs.getString("Social_number"));
					patient.setDoctor_no(rs.getString("Doctor_no"));
					list.add(patient);
				} while (rs.next());
			}
		} catch (Exception e) {
			System.out.println("PatienDao patientSearch() e.getMessage --> " + e.getMessage());
		} finally {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		}

		return list;
	}

	public ArrayList<ArrayList<String>> searchSet(String department, String doctorName, String reservationDate,
			String patientName) throws SQLException {

		ArrayList<ArrayList<String>> resultset = new ArrayList<ArrayList<String>>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sqlbase = "SELECT * " + "FROM patient p, " + "(select * from doctor where department like ?) d, "
				+ "(select * from doctor where doctor_name like ?) dd, "
				+ "(select * from reservation where reservation_date like ?) r " + "WHERE p.patient_name like ? "
				+ "AND p.doctor_no=dd.doctor_no " + "AND p.doctor_no=d.doctor_no";

		if (reservationDate != "")
			sqlbase = sqlbase.concat(" AND p.patient_no = r.patient_no");

		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sqlbase);
			pstmt.setString(1, "%" + department + "%");
			pstmt.setString(2, "%" + doctorName + "%");
			pstmt.setString(3, "%" + reservationDate + "%");
			pstmt.setString(4, "%" + patientName + "%");

			rs = pstmt.executeQuery();

			ArrayList<ArrayList<String>> set = new ArrayList<ArrayList<String>>();

			if (rs.next()) {
				do {
					ArrayList<String> setlist = new ArrayList<String>();
					setlist.add(rs.getString("Patient_no"));
					setlist.add(rs.getString("Doctor_no"));
					setlist.add(rs.getString("doctor_name"));
					setlist.add(rs.getString("department"));
					setlist.add(rs.getString("reservation_date"));
					set.add(setlist);
				} while (rs.next());
			}

			resultset.add(set.get(0));
			for (int i = 1; i < set.size(); i++) {
				if (set.get(i).get(1) != set.get(i - 1).get(1)) {
					resultset.add(set.get(i));
				}
			}

		} catch (Exception e) {
			System.out.println("check error -> " + e.getMessage());
		} finally {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		}
		return resultset;
	}

	// 환자등록

	public int regPatient(Patient patient) throws SQLException {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = 0;
		String sql = "insert into patient (patient_no, patient_name, social_number, birth, gender, contact, protector_contact, address, doctor_no)"
				+ " values(patient_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, 2)";
		/*
		 * String sql =
		 * "insert into patient (patient_no, patient_name, social_number, birth, gender, contact, protector_contact, address, doctor_no)"
		 * + " values(patient_seq.NEXTVAL, '?', '?', '?', '?', '?', '?', '?', ?)";
		 */
		
				
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, patient.getPatient_name());
			pstmt.setString(2, patient.getSocial_number());
			pstmt.setString(3, patient.getBirth());
			pstmt.setString(4, patient.getGender());
			pstmt.setString(5, patient.getContact());
			pstmt.setString(6, patient.getProtector_contact());
			pstmt.setString(7, patient.getAddress());
			
			//의사번호 2 대신 받기
			//pstmt.setString(8, patient.getDoctor_no());
			
			result = pstmt.executeUpdate();
			
			if (result > 0) {
				System.out.println("입력 성공");
			} else {
				System.out.println("입력실패");
			}
			
		} catch (SQLException e) {

			e.printStackTrace();
			System.out.println("PatientDao / regPatient Err" + e.getMessage());
		} finally {
			if (pstmt != null) pstmt.close();
			if (conn != null) conn.close();
		}
		return result;
	}

	/*
	 * doctor_no=2 라고 가정 public Patient select(String doctor_no, int patient_no)
	 * throws SQLException {
	 */
	public Patient selectPatient(int patient_no) throws SQLException {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// doctor_no=2 라고 가정
		String sql = "SELECT * FROM patient WHERE doctor_no = 2 AND patient_no=? ";

		Patient patient = new Patient();

		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			// doctor_no=2 라고 가정
			// pstmt.setInt(1, doctor_no);
			// pstmt.setInt(2, patient_no);
			pstmt.setInt(1, patient_no);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				patient.setPatient_no(rs.getInt("patient_no"));
				patient.setPatient_name(rs.getString("patient_name"));
				patient.setSocial_number(rs.getString("social_number"));
				patient.setBirth(rs.getString("birth"));
				patient.setGender(rs.getString("gender"));
				patient.setContact(rs.getString("contact"));
				patient.setProtector_contact(rs.getString("protector_contact"));
				patient.setAddress(rs.getString("address"));
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());

		} finally {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		}
		return patient;
	}
	
	public int updatePatient(Patient patient) throws SQLException {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;
		
		String sql = "UPDATE patient SET patient_name=?, social_number=?, birth=?, gender=?, contact=?, protector_contact=?, address=?, doctor_no=? WHERE patient_no=?";
		System.out.println("updatePatient Dao sql->" + sql);
		System.out.println("updatePatient Dao patient.getDoctor_no()->" + patient.getDoctor_no());
		System.out.println("updatePatient Dao patient.getPatient_no()->" + patient.getPatient_no());
			try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, patient.getPatient_name());
			System.out.println("updatePatient Dao patient.getPatient_name()->" + patient.getPatient_name());
			pstmt.setString(2, patient.getSocial_number());
			pstmt.setString(3, patient.getBirth());
			pstmt.setString(4, patient.getGender());
			pstmt.setString(5, patient.getContact());
			pstmt.setString(6, patient.getProtector_contact());
			pstmt.setString(7, patient.getAddress());
			pstmt.setString(8, patient.getDoctor_no());
			pstmt.setInt(9, patient.getPatient_no());
			
			result = pstmt.executeUpdate();
			System.out.println("updatePatient Dao result->" + result);
			
		} catch (Exception e) {
			System.out.println("updatePatient Dao e.getMessage->" + e.getMessage());
		} finally {
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		}
		
		return result;
		
	}
	
	public int deletePatient(int patient_no) throws SQLException {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;
		
		String sql = "DELETE FROM patient WHERE patient_no=?";
		System.out.println("delete Dao sql->" + sql);
		System.out.println("updatePatient Dao patient_no()->" + patient_no);
		
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, patient_no);
			System.out.println("patient_no()->" + patient_no);

			result = pstmt.executeUpdate();
			System.out.println("result->" + result);

		} catch (Exception e) {
			System.out.println("deletePatient Dao e.getMessage" + e.getMessage());
		} finally {
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		}
		
		return result;
		
	}
}
