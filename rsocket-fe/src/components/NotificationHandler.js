export default class NotificationHandler {

    requestStream(payload) {

        console.log(payload);
        
        if(payload.metadata.get(Metadata.ROUTE) === "file.notification.message") {
            const data = payload.data;
            return Flowable.just(make(data));
        }
   
      }

}