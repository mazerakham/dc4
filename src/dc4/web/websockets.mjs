export default class DC4Websockets {
  constructor() {
    this.socket = new WebSocket(WEBSOCKETS_URL);
    this.channels = {};
    this.socket.onmessage = this.handleMessage;
    this.listen("basic", "ping", (data) => {
      this.send({
        channel: "basic",
        command: "pong",
        data: {
          "a": 42
        }
      });
    });
  }

  send = (data) => {
    this.socket.send(JSON.stringify(data));
  }

  handleMessage = (event) => {
    console.log("Received an message from server! Here it is:");
    event = JSON.parse(event.data);
    console.log(event);
    if(!("channel" in event)) {
      console.log("Didn't have a channel. Can't process.");
      return;
    }
    if(!(event.channel in this.channels)) {
      console.log("Could not find the channel " + event.channel);
      return;
    }
    if(!("command" in event)) {
      console.log("Didn't have a command.  Can't process.");
      return;
    }
    if(!(event.command in this.channels[event.channel])) {
      console.log("This command " + event.command + " doesn't seem to be registered on channel " + event.channel);
      return;
    }
    if(!("data" in event)) {
      console.log("Didn't have data.  Can't process.");
      return;
    }

    // At this point the websockets command is validated and is passed to the callback logic.
    this.channels[event.channel][event.command](event.data);
  }

  listen = (channel, command, callback) => {
    console.log("Told to listen on channel " + channel + " for command " + command);
    if (!(channel in this.channels)) {
      this.channels[channel] = {};
    }
    this.channels[channel][command] = callback;
    console.log("Here are the channels now:");
    console.log(this.channels);
  };

  stopListening = (channel, command) => {
    if (!(channel in this.channels)) {
      return;
    }
    delete this.channels[command];
  }
}
