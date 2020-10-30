import React, { Component } from "react";
import ReactDOM from "react-dom";

import NotificationClient from '../../notification';
import NotificationHandler from './NotificationHandler'


class Login extends Component {
    constructor(props) {
      super(props);

      this.state = {
        notification: {}
      };

      this.notificationClient = new NotificationClient('ws://localhost:18080/rsocket', new NotificationHandler());
  
      this.handleLogin = this.handleLogin.bind(this);
      this.handleLogout = this.handleLogout.bind(this);
    }

    handleLogin(event) {

      
      
      this.notificationClient.connect()
            .then(sub => {
                return this.notificationClient.login("duminda");
            })
            .then(login => {
                console.log("======LoginContent=======");
                console.log(login);
                return this.notificationClient.streamPreviousFileNotifications("duminda");
    
            }).then(notif=>{
              console.log(notif);
              return this.notificationClient.streamFileNotifications();
            }).then(oldNotif=>{
              console.log(oldNotif);
            });
      
  }
  
    handleLogout(event) {

      /*TODO: disconnect user entirely */
      
      this.notificationClient.logout("duminda")
      .then(data=>{
        console.log(data);
        this.notificationClient.disconnect();
      })
    }
  
    render() {

      return (
  
        <div>
          <button onClick={this.handleLogin}>Login</button><br/>
          <button onClick={this.handleLogout}>Logout</button>
 
        </div>
         

      );
    }
  }
  
  export default Login;