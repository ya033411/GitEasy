package view;






import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class UserPageController {

/**************USER PAGE***********************************************************************************************/

    public static ObservableList<String> repos = FXCollections.observableArrayList();
    public static ObservableList<String> cloned_repos = FXCollections.observableArrayList();
    
    private ArrayList<String> clone_urls = new ArrayList<String>();
    private int generated_repo_count = 1;
    
    public void GoToUserPage() {
    	
    	if (GitEasyController.git_type.equals("github")) {
    		HttpResponse<JsonNode> response;
				try {
					response = Unirest.get("https://api.github.com/user/repos")
							  .header("Authorization", "token " + GitEasyController.acc_token)
							  .header("Accept", "application/vnd.github.v3+json")
							  .asJson();
					JSONArray repos_given = response.getBody().getArray();
					for (int i = 0; i < repos_given.length(); i++) {
						String repo_url = (String)(repos_given.getJSONObject(i).get("clone_url"));
						clone_urls.add(repo_url); 
						String repo_name = (String)(repos_given.getJSONObject(i).get("full_name"));
						repo_name = repo_name.substring(repo_name.indexOf("/")+1);
						repos.add(repo_name);
					}
					repo_list.setItems(repos);
					cloned_repo_list.setItems(cloned_repos);
				} catch (UnirestException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			

    	}
    	else {
    		HttpResponse<JsonNode> response;
			try {
				GitEasyController.Refresh_BitB_Token();
				response = Unirest.get("https://bitbucket.org/!api/2.0/repositories/" + GitEasyController.username + "?accesstoken=" + GitEasyController.acc_token )
						  .asJson();
				JSONArray repos_given =  response.getBody().getObject().getJSONArray("values");
				for (int i = 0; i < repos_given.length(); i++) {
					 JSONArray repo_links =  repos_given.getJSONObject(i).getJSONObject("links").getJSONArray("clone");
					 clone_urls.add(repo_links.getJSONObject(0).getString("href"));
					String repo_name = (String)(repos_given.getJSONObject(i).get("full_name"));
					repo_name = repo_name.substring(repo_name.indexOf("/")+1);
					repos.add(repo_name);
				}
				repo_list.setItems(repos);
				cloned_repo_list.setItems(cloned_repos);
			} catch (UnirestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	repo_list
		.getSelectionModel()
		.selectedIndexProperty()
		.addListener(
				(obs, oldVal, newVal) -> 
				CloneRepo()
				);
    }
    
    @FXML
    private Button Repo_Det_Button;
	
    @FXML
    private Button AddRepo_Button;

    @FXML
    private Button DeleteRepo_Button;

    @FXML
    private TextField Dir_Textfield;

    @FXML
    private Button Logout_Button;

    @FXML
    private ListView<String> cloned_repo_list;

    @FXML
    private ListView<String> repo_list;

    @FXML
    void AddRepo(ActionEvent event) {
    	String repo_name = "GeneratedRepo#" + generated_repo_count++;
    	if (GitEasyController.git_type.equals("github")) {
    		
		
				Unirest.post("https://api.github.com/user/repos")
						  .header("Authorization", "token " + GitEasyController.acc_token)
						  .header("Accept", "application/vnd.github.v3+json")
				          .body("{\"name\":\"" + repo_name + "\"}");
			
    	}
    	else {
    		GitEasyController.Refresh_BitB_Token();
    		Unirest.post("https://api.bitbucket.org/2.0/repositories/" + GitEasyController.BitB_workspace + "/" + repo_name)
			  .header("Authorization", "Bearer " + GitEasyController.acc_token)
			  .body("{\"scm\":\"git\"}");
    	}
    	repos.add(repo_name);

    }

    @FXML
    void DeleteRepo(ActionEvent event) {
    	int index = repo_list.getSelectionModel().getSelectedIndex();
    	String repo_name = repos.get(index);
    	if (GitEasyController.git_type.equals("github")) {
    		Unirest.delete("https://api.github.com/repos/" + GitEasyController.username + "/" + repo_name)
			  .header("Authorization", "token " + GitEasyController.acc_token)
			  .header("Accept", "application/vnd.github.v3+json");
    	}
    	else {
    		GitEasyController.Refresh_BitB_Token();
    		Unirest.delete("https://api.bitbucket.org/2.0/repositories/" + GitEasyController.BitB_workspace + "/" + repo_name)
			  .header("Authorization", "Bearer " + GitEasyController.acc_token);
			  
    	}
    	repos.remove(index);
    }

    @FXML
    void LogoutUser(ActionEvent event) {
    	System.exit(0);
    }
    
    public void CloneRepo() {
    	int index = repo_list.getSelectionModel().getSelectedIndex();
    	 cloned_repos.add(repos.get(index));
    	 String dir = Dir_Textfield.getText();
    	 if (new File(dir).mkdirs()) {
    		 try {
				Git git = Git.cloneRepository()
						  .setURI(clone_urls.get(index))
						  .setDirectory(new File(dir))
						  .call();
				GitEasyController.currentRepo = git;
				
			} catch (InvalidRemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GitAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		 
    	 }
    	 else {
    		 throw new IllegalArgumentException();
    	 }
    	 
    }
    
    @FXML
    void GoToRepoDetPage(ActionEvent event) throws IOException, GitAPIException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("GitEasy_repodetailspage.fxml"));
   	    GitEasyController.root = loader.load();
		RepoDetailsPageController repodetcontr = loader.getController();
		repodetcontr.SetUpRepoDetsPage();
		GitEasyController.stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		GitEasyController.scene = new Scene(GitEasyController.root);
		GitEasyController.stage.setScene(GitEasyController.scene);
		GitEasyController.stage.show();
    }

    
    
    
}
