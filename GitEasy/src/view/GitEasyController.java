package view;


import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class GitEasyController {
	
	public static String username;
	public static String password;
	public static String git_type;
	public static String acc_token_name;
	public static String acc_token;
	public static String refresh_token;
	public static String BitB_key;
	public static String BitB_secret;
	public static String BitB_workspace;
	public static Git currentRepo;
	
	static Stage stage;
	static Scene scene;
	static Parent root;
	
	/**************WELCOME PAGE***********************************************************************************************/
	
    @FXML
    private CheckBox BitB_CheckBox;

    @FXML
    private TextField Username_Login;

    @FXML
    private CheckBox Github_CheckBox;

    @FXML
    private Button Login_Button;

    @FXML
    private TextField Password_Login;

    @FXML
    void LoginUser(ActionEvent event) throws IOException {
    	 username = Username_Login.getText();
    	 password = Password_Login.getText();
    	String query = "SELECT A.GIT_TYPE,A.TOKEN_NAME, A.TOKEN, A.REFRESH_TOKEN, A.BITB_KEY, A.BITB_SECRET, A.BITBWORKSPACE, U.USERNAME, U.PASSWORD " +
    			       "FROM USER U JOIN AUTH A ON U.ID = A.ID WHERE U.USERNAME = " +
    			       "'" + username + "' " +
    			       "AND U.PASSWORD = " + 
    			       "'" + password + "'"; 
    	
    	String dburl = "jdbc:mysql://localhost:3306/GitEasyAuth?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    	String dbuser = "root";
    	String dbpassword = "Jvyhas123$";

    	if (Github_CheckBox.isSelected()) {
    		git_type = "github";
    		query += " AND A.GIT_TYPE = " +
    	             "'" + git_type + "'";
    		try {
    			Connection myConn = DriverManager.getConnection(dburl,dbuser,dbpassword);
    			Statement stmt=myConn.createStatement();  
    			ResultSet rs=stmt.executeQuery(query);
    			while (rs.next()) {
    			acc_token_name = rs.getString("token_name");
    		    acc_token = rs.getString("token");
    			}
    			myConn.close();  
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
    	}
    	else {
    		git_type = "bitbucket";
    		
    		query += " AND A.GIT_TYPE = " +
   	             "'" + git_type + "'";
    		try {
    			Connection myConn = DriverManager.getConnection(dburl,dbuser,dbpassword);
    			Statement stmt=myConn.createStatement();  
    			ResultSet rs=stmt.executeQuery(query);
    			while (rs.next()) {
        		    acc_token = rs.getString("token");
        		    refresh_token = rs.getString("refresh_token");
        		    BitB_key = rs.getString("bitb_key");
        		    BitB_secret = rs.getString("bitb_secret");
        		    BitB_workspace = rs.getString("bitbworkspace");
        			}
                myConn.close();  
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
    	}
    	
    	 	FXMLLoader loader = new FXMLLoader(getClass().getResource("GitEasy_userpage.fxml"));
    	 	root = loader.load();	
    		
    		UserPageController userpagecontr = loader.getController();
    		userpagecontr.GoToUserPage();

    		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    		scene = new Scene(root);
    		stage.setScene(scene);
    		stage.show();
    	
    }
    
    @FXML
    void BitbucketSelected(ActionEvent event) throws InvalidRemoteException, TransportException, GitAPIException, MissingObjectException, IncorrectObjectTypeException, IOException {

			
	
    	if (BitB_CheckBox.isSelected()) {
    		Github_CheckBox.setDisable(true);
    	}
    	else {
    		Github_CheckBox.setDisable(false);
    	}
    	
    }

    @FXML
    void GithubSelected(ActionEvent event) {
    	
    	if (Github_CheckBox.isSelected()) {
    		BitB_CheckBox.setDisable(true);
    	}
    	else {
    		BitB_CheckBox.setDisable(false);
    	}
    	
    }
    
    public static void Refresh_BitB_Token() {
    	HttpResponse<JsonNode> response;
		try {
			response = Unirest.post("https://bitbucket.org/site/oauth2/access_token")
					  .header("Content-Type", "application/x-www-form-urlencoded")
					  .basicAuth(GitEasyController.BitB_key,GitEasyController.BitB_secret)
					  .field("grant_type", "refresh_token")
					  .field("refresh_token", GitEasyController.refresh_token)
					  .asJson();
			JSONObject output = response.getBody().getObject();
			GitEasyController.acc_token = output.getString("access_token");
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }

}



