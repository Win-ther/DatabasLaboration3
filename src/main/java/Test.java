import java.sql.*;
import java.util.Scanner;

public class Test {

    private static Scanner scanner = new Scanner(System.in);

    private static Connection connect() {
        // SQLite connection string
        //String url = "jdbc:sqlite:/Users/jerryjohansson/Desktop/sqlite-demo/sqldemo.db";
        String url = "jdbc:sqlite:/Users/emilw/DataGripProjects/SQLiteLaboration3/Labb3.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private static void printActions() {
        System.out.println("\nVälj:\n");
        System.out.println("0  - Stäng av\n" +
                "1  - Visa alla böcker\n" +
                "2  - Lägga till en ny bok\n" +
                "3  - Uppdatera en bok\n" +
                "4  - Ta bort en bok\n" +
                "5  - Visa en lista över alla val.");
    }


    private static void deleteBook(){
        System.out.println("Skriv in id:t på boken som ska tas bort: ");
        int inputId = scanner.nextInt();
        delete(inputId);
        scanner.nextLine();
    }

    private static void selectAll(){
        String sql = "SELECT * FROM bok";

        try {
            Connection conn = connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("bokId") +  "\t" +
                        rs.getString("bokTitel") + "\t" +
                        rs.getString("bokForfattare") + "\t" +
                        rs.getString("bokPris"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void insert(String titel, String forfattare, int pris) {
        String sql = "INSERT INTO bok(bokTitel, bokForfattare, bokPris) VALUES(?,?,?)";

        try{
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, titel);
            pstmt.setString(2, forfattare);
            pstmt.setInt(3, pris);
            pstmt.executeUpdate();
            System.out.println("Du har lagt till en ny bok");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void update(String forfattare, String titel, int pris, int id) {
        String sql = "UPDATE bok SET bokForfattare = ? , "
                + "bokTitel = ? , "
                + "bokPris = ? "
                + "WHERE bokId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setString(1, forfattare);
            pstmt.setString(2, titel);
            pstmt.setInt(3, pris);
            pstmt.setInt(4, id);
            // update
            pstmt.executeUpdate();
            System.out.println("Du har uppdaterat vald bok");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void delete(int id) {
        String sql = "DELETE FROM bok WHERE bokId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setInt(1, id);
            // execute the delete statement
            pstmt.executeUpdate();
            System.out.println("Du har tagit bort boken");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {

        boolean quit = false;
        printActions();
        while(!quit) {
            System.out.println("\nVälj (5 för att visa val):");
            int action = scanner.nextInt();
            scanner.nextLine();

            switch (action) {
                case 0:
                    System.out.println("\nStänger ner...");
                    quit = true;
                    break;

                case 1:
                    selectAll();
                    break;

                case 2:
                    insert("Sagan om ringen", "Tolkien, J.R.R", 120);
                    break;

                case 3:
                    update("Bilbo", "Tolkien, J.R.R", 100, 1);
                    break;

                case 4:
                    //delete(1);
                    deleteBook();
                    break;

                case 5:
                    printActions();
                    break;
            }
        }

    }

}
