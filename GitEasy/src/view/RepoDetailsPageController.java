package view;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.ServiceUnavailableException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class RepoDetailsPageController {

/**************REPO DETAILS PAGE***********************************************************************************************/
    
	public static ObservableList<String> files_unadded;
    public static ObservableList<String> files_added;
    
    @FXML
    private Button Add_Files_Button;

	
    @FXML
    private Button Back_Button;

    @FXML
    private Button Commit_Button;

    @FXML
    private Button Push_Button;

    @FXML
    private ListView<String> files_added_list;

    @FXML
    private ListView<String> files_unadded_list;
    
    @FXML
    void AddFiles(ActionEvent event) throws NoFilepatternException, GitAPIException {
        for (int i = 0; i < files_unadded.size(); i++) {
        	// add file from not added file list
        	GitEasyController.currentRepo.add().addFilepattern(files_unadded.get(i)).call();
        	files_added.add(files_unadded.get(i));

        }
    }

    @FXML
    void BacktoUserPage(ActionEvent event) throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("GitEasy_userpage.fxml"));
   	    GitEasyController.root = loader.load();
		UserPageController userpagecontr = loader.getController();
		userpagecontr.GoToUserPage();
		GitEasyController.stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		GitEasyController.scene = new Scene(GitEasyController.root);
		GitEasyController.stage.setScene(GitEasyController.scene);
		GitEasyController.stage.show();
    }

    @FXML
    void CommitChanges(ActionEvent event) throws AbortedByHookException, ConcurrentRefUpdateException, NoHeadException, NoMessageException, ServiceUnavailableException, UnmergedPathsException, WrongRepositoryStateException, GitAPIException {
    	CommitCommand commit = GitEasyController.currentRepo.commit();
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
    	   LocalDateTime now = LocalDateTime.now();  
    	  String curTime =  dtf.format(now);  
    	commit.setMessage(curTime).call();
    }

    @FXML
    void PushChanges(ActionEvent event) throws InvalidRemoteException, TransportException, GitAPIException {
    	if (GitEasyController.git_type.equals("github")) {
    		GitEasyController.currentRepo.push().setRemote("origin").add("main").call();
    	}
    	else {
    		GitEasyController.currentRepo.push().setRemote("origin").add("master").call();
    	}
    	
    }

	public void SetUpRepoDetsPage() throws GitAPIException, MissingObjectException, IncorrectObjectTypeException, IOException {
		// TODO Auto-generated method stub
		RevCommit youngestCommit = null;
    	List<Ref> branches = GitEasyController.currentRepo.branchList().setListMode(ListMode.ALL).call();
    	    RevWalk walk = new RevWalk(GitEasyController.currentRepo.getRepository());
    	    for(Ref branch : branches) {
    	        RevCommit commit = walk.parseCommit(branch.getObjectId());
    	        if(youngestCommit == null || commit.getAuthorIdent().getWhen().compareTo(
    	           youngestCommit.getAuthorIdent().getWhen()) > 0)
    	           youngestCommit = commit;
    	    }
    	    TreeWalk treeWalk = new TreeWalk( GitEasyController.currentRepo.getRepository() );
    	    treeWalk.reset( youngestCommit.getTree().getId() );
    	    while( treeWalk.next() ) {
    	      String path = treeWalk.getPathString();
    	     files_unadded.add(path);
    	    }
    	    treeWalk.close();
	        files_unadded_list.setItems(files_unadded);
	        files_added_list.setItems(files_added);
		
	}
}
