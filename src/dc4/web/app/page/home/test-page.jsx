import { React, ReactDOM, useEffect, useState } from "react.mjs";

export default function TestPage(props) {

  const [count, setCount] = useState(0);
  const [globalCount, setGlobalCount] = useState(null);
  const [gotResponse, setGotResponse] = useState(false);
  const [a, setA] = useState(null);
  const websockets = props.websockets;

  websockets.listen("matchmaking", "matchFound", (data) => {
    console.log("Received matchmaking response: " + data);
    props.startGame(data);
  });

  const sendRequest = () => {
    $.get("/hello").done(data => {
      setA(data.a);
      setGotResponse(true);
    });
  };

  const incGlobalCount = () => {
    $.post("/counter").done(data => {
      setGlobalCount(data.newCount);
    });
  };

  const sendWebsocketMessage = () => {
    websockets.send({
      channel: "basic",
      command: "hello",
      data: {
        msg: "A message from home.jsx's sendWebsocketMessage() function."
      }
    });
  };

  const enqueue = () => {
    websockets.send({
      channel: "matchmaking",
      command: "enqueue",
      data: {}
    });
  };

  useEffect(() => {
    $.get("/counter").done(data => {
      setGlobalCount(data.count);
    });
  });

  return (
    <div>
      <div>Hello world in a component.</div>
      <div>You clicked {count} times.</div>
      {globalCount && <div>Global counter is at {globalCount}</div>}
      <button onClick={() => setCount(count + 1)}>Click me.</button>
      <button onClick={() => sendRequest()}>Send API request.</button>
      { gotResponse && (
        <div>
          The server told us the answer.  It's {a}!
        </div>
      )}
      <button onClick={() => incGlobalCount()}>Increment global counter.</button>
      <button onClick={() => sendWebsocketMessage()}>Send websocket message.</button>
      <button onClick={() => enqueue()}>Enqueue.</button>

    </div>
  );
}
