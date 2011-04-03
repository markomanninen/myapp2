(ns appengine-magic.services.channel
  (:refer-clojure :exclude [send])
  (:import [com.google.appengine.api.channel ChannelServiceFactory ChannelMessage]))


(defonce *channel-service* (atom nil))


(defn get-channel-service []
  (when (nil? @*channel-service*)
    (reset! *channel-service* (ChannelServiceFactory/getChannelService)))
  @*channel-service*)


(defn create-channel
  "Returns a channel token."
  [^String client-id]
  (.createChannel (get-channel-service) client-id))


(defn make-message [^String client-id, ^String message]
  (ChannelMessage. client-id message))


(defn send
  ([^ChannelMessage message]
     (.sendMessage (get-channel-service) message))
  ([^String client-id, ^String message]
     (send (make-message client-id message))))
