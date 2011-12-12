import java.util.*;
import java.io.*;
import java.rmi.*;
import java.rmi.server.*;
/**
 * classe représentant l'objet servant de la fabrique
 */
public class FabriqueImpl implements Fabrique {
	/**
    la structure de mémoristion des forums
	 */
	private static ArrayList<Forum> forums = new ArrayList<Forum>();
	/**
	 * l'identifiant unique d'intervenant
	 */	
	protected Integer portCounter = new Integer(1235);
	
	public FabriqueImpl() throws RemoteException {
		super();
	}
	
	public synchronized ArrayList<Forum> create(String forumName) throws RemoteException{
		boolean alreadyExists = false;
		
		for(Iterator<Forum> i=forums.iterator(); i.hasNext(); ) {
			Forum forumAux = i.next();
			if(forumAux.getForumName().equals(forumName))
				alreadyExists = true;
		}
		
		if(!alreadyExists) {
			Forum forum = new ForumImpl(forumName, portCounter);
			
			forums.add(forum);	
			portCounter++;
			forum.start();
		}
		
		return forums;
	}
	
	public synchronized ArrayList<Forum> destroy(String forumName) throws RemoteException{
		boolean forumFound = false;
		  int i= 0;
		  do{
			  if(forumName.equals(forums.get(i).getForumName()))
				  forumFound = true;
			  else
				  i++;
		  }while(!forumFound && (i < forums.size()));
		
		if(forumFound) {
			forums.get(i).stop();
			forums.remove(forums.get(i));	
		}
		
		return forums;
	}
	
	  public synchronized Forum getForum(String forumName) throws RemoteException {
		  boolean forumFound = false;
		  int i= 0;
		  if(!forums.isEmpty()){
			  do{
				  if(forumName.equals(forums.get(i).getForumName()))
					  forumFound = true;
				  else
					  i++;
			  }while(!forumFound && (i < forums.size()));
			  
			  if(forumFound)
				  return forums.get(i);
	  	}
		  return null;
	  }

	public String listForums() {
		StringBuilder sBuilder = new StringBuilder();
		
		for(Iterator<Forum> i=forums.iterator(); i.hasNext(); ) {
			Forum forumAux = i.next();
			try {
				sBuilder.append(forumAux.getForumName());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			if (i.hasNext())
		        sBuilder.append("\n");
		}
		return sBuilder.toString();
	}

}

