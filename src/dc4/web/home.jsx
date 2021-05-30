import { React, ReactDOM, useEffect, useState } from "react.mjs";
import DC4Websockets from "websockets.mjs";

function Home() {

  const [count, setCount] = useState(0);
  const [globalCount, setGlobalCount] = useState(null);
  const [gotResponse, setGotResponse] = useState(false);
  const [a, setA] = useState(null);
  const websockets = new DC4Websockets();

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

    </div>
  );
}

ReactDOM.render(<Home />, document.querySelector("[home-app]"));