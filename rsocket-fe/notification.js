  
import {RSocketClient, JsonSerializer} from 'rsocket-core';
import RSocketWebSocketClient from 'rsocket-websocket-client';
import Metadata, {JsonMetadataSerializer} from './metadata'


//TODO: https://github.com/rsocket/rsocket-js/blob/master/packages/rsocket-examples/src/ResumeExample.js

class NotificationClient{

    constructor(url, responder){

        this.client = new RSocketClient({
            serializers: {
                data: JsonSerializer,
                metadata: JsonMetadataSerializer,
            },
            setup: {
                // ms btw sending keepalive to server
                keepAlive: 10000,
                // ms timeout if no keepalive response
                lifetime: 20000,
                dataMimeType: 'application/json',
                metadataMimeType: JsonMetadataSerializer.MIME_TYPE,
            },
            transport: new RSocketWebSocketClient({url: url})
            ,responder: responder
        });

    }

    connect(cb) {
        return new Promise(((resolve, reject) => {
            this.client.connect().subscribe({
                onComplete: s => {
                    this.socket = s;
                    resolve(this.socket);
                },
                onError: error => reject(error),
                onSubscribe: cancel => { this.cancel = cancel}
            });
        }));
    }

    login(user) {
        return new Promise((resolve, reject) => {
            let metadata = new Metadata();
            metadata.set(Metadata.ROUTE, `login`);
            this.socket.requestResponse({
                metadata: metadata,
                data: user
            }).subscribe({
                onComplete: msg => resolve(msg.data),
                onError: error => reject(error)
            });
        });
    }

    logout(user) {
        return new Promise((resolve, reject) => {
            let metadata = new Metadata();
            metadata.set(Metadata.ROUTE, `logout`);
            this.socket.requestResponse({
                metadata: metadata,
                data: user
            }).subscribe({
                onComplete: msg => resolve(msg.data),
                onError: error => reject(error)
            });
        });
    }

    streamFileNotifications() {
        console.log("streamFileNotifications");
        
        let fileNotifications = [];
        return new Promise(((resolve, reject) => {
            let metadata = new Metadata();
            metadata.set(Metadata.ROUTE, 'notification.message');
            return this.socket.requestStream({ 
                metadata: metadata,
            }).subscribe({
                onSubscribe: sub => {
                   // console.log(sub);
                    sub.request(2147483647)
                },
                onError: error => {
                    console.error(error);
                    reject(error)
                },
                onNext: msg => {
                    console.log(msg);
                    fileNotifications.push(msg.data)
                },
                onComplete: () => {
                    console.log("requestStream done"),
                    resolve(fileNotifications)
                }
            });
        }));
    }

    streamPreviousFileNotifications(receiver) {
        console.log("Old FileNotifications");
        
        let oldFileNotifications = [];
        return new Promise(((resolve, reject) => {
            let metadata = new Metadata();
            metadata.set(Metadata.ROUTE, `notification.message.${receiver}`);
            return this.socket.requestStream({ 
                metadata: metadata,
            }).subscribe({
                onSubscribe: sub => {
                  //  console.log(sub);
                    sub.request(2147483647)
                },
                onError: error => {
                    console.error(error);
                    reject(error)
                },
                onNext: msg => {
                   // console.log(msg);
                    oldFileNotifications.push(msg.data)
                },
                onComplete: () => {
                    console.log("request old messages done"),
                    resolve(oldFileNotifications)
                }
            });
        }));
    }

    disconnect() {
       console.log(this.socket);
        this.cancel();
    }
}
function make(data) {
    return {
      data,
      metadata: '',
    };
  }



export default NotificationClient;