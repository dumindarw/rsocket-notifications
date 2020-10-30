package lk.drw.rsocket.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Notification {

  int code;

  String note;

  String sender;

  String receiver;

  long publishedTime;

}