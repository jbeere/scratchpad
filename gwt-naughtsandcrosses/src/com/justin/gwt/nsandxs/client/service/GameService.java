package com.justin.gwt.nsandxs.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.core.client.GWT;

/**
 * @author justin
 */
@RemoteServiceRelativePath("NaughtsAndCrossesService")
public interface GameService extends RemoteService {

   int join();

   void move(int gameId, int pos);


   /**
    * Utility/Convenience class.
    * Use GameService.App.getInstance() to access static instance of NaughtsAndCrossesServiceAsync
    */
   public static class App {
      private static final GameServiceAsync ourInstance = (GameServiceAsync) GWT.create(GameService.class);

      public static GameServiceAsync getInstance() {
         return ourInstance;
      }
   }
}
