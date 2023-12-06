import java.sql.*;
import java.util.Scanner;

public class Labb3 {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int choice;
        printActions();
        loop:
        do {
            System.out.println("\nAwaiting input(5 for options):");
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Incorrect input, try again");
                choice = 5;
            }
            switch (choice) {
                case 0 -> {
                    System.out.println("Exiting program");
                    break loop;
                }
                case 1 -> selectQueries();
                case 2 -> insertQueries();
                case 3 -> updateQueries();
                case 4 -> deleteQueries();
                case 5 -> printActions();
                case 6 -> favouriteGame();
                case 7 -> removeFavouriteFromGame();
                case 8 -> showNrOfGamesInDB();
                case 9 -> searchForGame();
            }
        } while (true);
    }

    private static void searchForGame() {
        System.out.println("Enter name of game you want to search for");
        String name = scanner.nextLine();
        String sql = "SELECT game.gameName,game.gameReleaseYear,review.reviewName,review.reviewScore FROM game " +
                "LEFT JOIN review ON game.gameId = review.reviewGameId " +
                "WHERE game.gameName LIKE ?";
        try(Connection connection = connect()){
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1,name);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                System.out.println(rs.getString("gameName") + "\t" +
                        rs.getInt("gameReleaseYear") + "\t/\t" +
                        rs.getString("reviewName") + " - " +
                        rs.getInt("reviewScore"));
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static void showNrOfGamesInDB() {
        String sql = "SELECT COUNT(gameId) FROM game";

        try (Connection connection = connect()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("There are "+rs.getInt(1)+" saved games in the database right now.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void removeFavouriteFromGame() {
        System.out.println("Insert ID for the game you want to remove from favourites");
        int id = getId();

        String sql = "UPDATE game SET gameFavourite = 0 " +
                "WHERE gameId = ?";
        try (Connection connection = connect()) {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Game removed from favourites!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void favouriteGame() {
        System.out.println("Insert ID for the game you want to mark as favourite");
        int id = getId();

        String sql = "UPDATE game SET gameFavourite = 1 " +
                "WHERE gameId = ?";
        try (Connection connection = connect()) {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Game set as favourite!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static int getId() {
        int id = 0;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Incorrect input, going back to main menu");
            printActions();
        }
        return id;
    }

    private static void deleteQueries() {
        System.out.println("""
                1. Delete game
                2. Delete review
                """);
        int choice = getChoice();
        switch (choice) {
            case 1 -> deleteGame();
            case 2 -> deleteReview();
        }
    }

    private static void deleteReview() {
        System.out.println("Insert ID for the review you want to delete");
        int id = getId();
        String sql = "DELETE FROM review WHERE reviewId = ?";

        try (Connection connection = connect();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Review removed!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deleteGame() {
        System.out.println("Insert ID for the game you want to delete");
        int id = getId();
        String sql = "DELETE FROM game WHERE gameId = ?";

        try (Connection connection = connect();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Game removed!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void updateQueries() {
        System.out.println("""
                1. Update game
                2. Update review
                """);
        int choice = getChoice();
        switch (choice) {
            case 1 -> updateGame();
            case 2 -> updateReview();
        }
    }

    private static int getChoice() {
        int choice = 0;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Incorrect input, going back to main menu");
            printActions();
        }
        return choice;
    }

    private static void updateReview() {
        System.out.println("Insert ID for the review you want to update");
        int id = getId();
        int score;
        System.out.println("Insert new name for reviewer");
        String name = scanner.nextLine();
        System.out.println("Insert new score for game");
        try {
            score = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Incorrect input, going back to main menu");
            printActions();
            return;
        }
        String sql = "UPDATE review SET reviewName = ?, " +
                "reviewScore = ?" +
                "WHERE reviewId = ?";
        try (Connection connection = connect()) {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setInt(2, score);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
            System.out.println("Review updated!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void updateGame() {
        System.out.println("Insert ID for game you want to update");
        int id = getId();
        System.out.println("Insert new name for the game");
        String name = scanner.nextLine();
        System.out.println("Insert new release year for the game");
        int releaseYear;
        try {
            releaseYear = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Incorrect input, going back to main menu");
            printActions();
            return;
        }
        String sql = "UPDATE game SET gameName = ?, " +
                "gameReleaseYear = ?" +
                "WHERE gameId = ?";
        try (Connection connection = connect()) {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setInt(2, releaseYear);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
            System.out.println("Game updated!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void insertQueries() {
        System.out.println("""
                1. Insert game
                2. Insert review
                """);
        int choice = getChoice();
        if (choice == 0)
            return;
        switch (choice) {
            case 1 -> insertGame();
            case 2 -> insertReview();
        }
    }

    private static void insertReview() {
        System.out.println("Insert name for reviewer");
        String name = scanner.nextLine();
        int score;
        int id;
        try {
            System.out.println("Insert score for game(0 - 10)");
            score = Integer.parseInt(scanner.nextLine());
            System.out.println("Insert ID for the game you are creating a review for");
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Incorrect input, going back to main menu");
            printActions();
            return;
        }

        String sql = "INSERT INTO review(reviewName, reviewScore, reviewGameId) VALUES(?,?,?)";

        try (Connection connection = connect()) {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setInt(2, score);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
            System.out.println("Review added!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void insertGame() {
        System.out.println("Insert name for game");
        String name = scanner.nextLine();
        System.out.println("Insert release year for game");
        int releaseYear;
        try {
            releaseYear = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Incorrect input, going back to main menu");
            printActions();
            return;
        }
        String sql = "INSERT INTO game(gameName, gameReleaseYear) VALUES(?,?)";

        try (Connection connection = connect()) {

            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setInt(2, releaseYear);
            pstmt.executeUpdate();
            System.out.println("Game added!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void selectQueries() {
        String sql;
        System.out.println("""
                1. Show all games
                2. Show all reviews
                3. Show all games with their reviews
                4. Show favourites
                """);
        int choice = getChoice();
        if (choice <= 0 ||choice >=5) {
            System.out.println("Incorrect input, returning to menu");
            printActions();
            return;
        }
        sql = getSelectSqlQuery(choice);
        if (sql == null)
            return;
        try (Connection connection = connect()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                switch (choice) {
                    case 1 -> selectGames(rs);
                    case 2 -> selectReviews(rs);
                    case 3, 4 -> selectGamesAndReviews(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String getSelectSqlQuery(int choice) {
        switch (choice) {
            case 1 -> {
                return  "SELECT * FROM game";
            }
            case 2 -> {
                return "SELECT * FROM review";
            }
            case 3 -> {
                return "SELECT game.gameName, game.gameReleaseYear, review.reviewName,review.reviewScore FROM game " +
                                "INNER JOIN review ON game.gameId = review.reviewGameId";
            }
            case 4 -> {
                return "SELECT game.gameName, game.gameReleaseYear, review.reviewName,review.reviewScore FROM game " +
                                "LEFT JOIN review ON game.gameId = review.reviewGameId WHERE game.gameFavourite = 1";
            }
        }
        return null;
    }

    private static void selectGamesAndReviews(ResultSet rs) throws SQLException {
        System.out.println(rs.getString("gameName") + "\t" +
                rs.getInt("gameReleaseYear") + "\t/\t" +
                rs.getString("reviewName") + " - " +
                rs.getInt("reviewScore"));
    }

    private static void selectReviews(ResultSet rs) throws SQLException {
        System.out.println(rs.getInt("reviewId") + "\t" +
                rs.getString("reviewName") + " - " +
                rs.getInt("reviewScore") + "\t" +
                rs.getInt("reviewGameId"));
    }

    private static void selectGames(ResultSet rs) throws SQLException {
        System.out.println(rs.getInt("gameId") + "\t" +
                rs.getString("gameName") + "\t" +
                rs.getInt("gameReleaseYear"));
    }

    private static void printActions() {
        System.out.println("""
                0 - Exit program
                1 - Show
                2 - Add
                3 - Update
                4 - Delete
                5 - Print choices
                6 - Favourite a game
                7 - Remove favourite from a game
                8 - Show number of games in database
                9 - Search for a game""");
    }

    private static Connection connect() {
        String url = "jdbc:sqlite:/Users/emilw/Desktop/DATABAS KURS/Laboration 3/Labb3.db";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }
}
