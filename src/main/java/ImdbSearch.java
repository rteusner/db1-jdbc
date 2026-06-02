import entities.Actor;
import entities.Movie;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class ImdbSearch {

	public static void main(String[] args) {
		try (Connection connection = openConnection()) {

			String output = "";
			output += selectFirstMovies(connection, 10);
			output += "\n";
			System.out.println(output);

		} catch (IOException e) {
			System.err.println("Could not load properties file.");
			System.exit(1);
		} catch (SQLException e) {
			System.err.println("Error message: " + e.getMessage());
			System.err.println("SQLSTATE: " + e.getSQLState());
			System.err.println("Error code: " + e.getErrorCode());
			System.exit(1);
		}
	}

	public static ConnectionConfig getConnectionConfig() throws IOException {
		Properties props = new Properties();

		//load a properties file from class path, inside static method
		try (InputStream in = ImdbSearch.class.getClassLoader()
				.getResourceAsStream("database.properties")) {
			if (in == null) {
				throw new IOException("database.properties not found on classpath");
			}
			props.load(in);
		}

		String host = props.getProperty("host");
		String port = props.getProperty("port");
		String database = props.getProperty("database");
		String username = props.getProperty("username");
		String password = props.getProperty("password");

		return new ConnectionConfig(username, password, host, Integer.parseInt(port), database);

	}

	public static Connection openConnection() throws SQLException, IOException {

		ConnectionConfig cc = getConnectionConfig();
		return DriverManager.getConnection("jdbc:postgresql://" + cc.host() + ":"
				+ cc.port() +"/" + cc.database(), cc.username(), cc.password());
	}

	private static String selectFirstMovies(Connection connection, int limit)
			throws SQLException {
		StringBuilder output = new StringBuilder("First " + limit + " MOVIES\n");
		// fetch the earliest movies by start year
		try (PreparedStatement stmt = connection.prepareStatement(
				"SELECT tconst, \"primaryTitle\", \"isAdult\", \"startYear\", "
				+ "\"runtimeMinutes\", genres FROM tmovies ORDER BY \"startYear\" ASC LIMIT ?")) {
			stmt.setInt(1, limit);

			try (ResultSet movies = stmt.executeQuery()) {
				while (movies.next()) {
					// build output string
					output.append(movies.getString("tconst")).append(", ")
							.append(movies.getString("primaryTitle")).append(", ")
							.append(movies.getString("isAdult")).append(", ")
							.append(movies.getString("startYear")).append(", ")
							.append(movies.getString("runtimeMinutes")).append(", ")
							.append(movies.getString("genres")).append("\n");
				}
			}
		}
		return output.toString();
	}

	protected static List<Movie> findMovies(Connection connection, String keyword)
			throws SQLException {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	protected static List<Actor> findActors(Connection connection, String keyword)
			throws SQLException {
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
