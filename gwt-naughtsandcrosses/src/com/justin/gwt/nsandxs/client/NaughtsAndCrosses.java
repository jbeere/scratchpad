package com.justin.gwt.nsandxs.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.justin.gwt.nsandxs.client.model.GameModel;
import com.justin.gwt.nsandxs.client.model.Symbol;
import com.justin.gwt.nsandxs.client.service.GameService;
import com.justin.gwt.nsandxs.client.service.GameServiceAsync;
import com.justin.gwt.nsandxs.client.view.GameView;
import com.justin.gwt.nsandxs.client.view.GameViewListener;

/**
 * jeb 11 Feb 2010 12:46:48 PM
 */
public class NaughtsAndCrosses implements EntryPoint {

   private int gameId = -1;

   public void onModuleLoad() {

      final GameModel model = new GameModel();
      final GameView view = new GameView(model);

      final Label labelThis = new Label();
      final Label labelThat = new Label();

      final GameServiceAsync service = GameService.App.getInstance();

      view.addListener(new GameViewListener() {
         public void boxSelected(int pos) {
            model.place(((gameId++) % 3) == 0 ? Symbol.CROSS: Symbol.NAUGHT, pos);
            /*service.move(gameId, pos, new AsyncCallback<Void>() {
               public void onSuccess(Void result) {
                  
               }

               public void onFailure(Throwable caught) {
                  fail(caught);
               }
            });*/
         }
      });


      FlexTable table = new FlexTable();

      table.setWidget(0, 0, labelThis);
      table.setWidget(1, 0, view);
      table.setWidget(2, 0, labelThat);

      RootPanel.get().add(table);

      service.join(new AsyncCallback<Integer>() {
         public void onSuccess(Integer result) {
            gameId = result;
         }

         public void onFailure(Throwable caught) {
            fail(caught);
         }
      });
   }

   private void fail(Throwable caught) {
      //To change body of created methods use File | Settings | File Templates.
   }
}
