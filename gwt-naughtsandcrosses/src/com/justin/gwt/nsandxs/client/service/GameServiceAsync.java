package com.justin.gwt.nsandxs.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author justin
 */
public interface GameServiceAsync {


   void join(AsyncCallback<Integer> async);

   void move(int gameId, int pos, AsyncCallback<Void> async);
}
