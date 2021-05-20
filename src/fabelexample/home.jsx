import { React, ReactDOM, useEffect, useState } from "react.mjs";

function Home() {

  const [count, setCount] = useState(0);
  const [gotResponse, setGotResponse] = useState(false);
  const [a, setA] = useState(null);

  const sendRequest = () => {
    $.get("http://localhost:7070/hello").done(data => {
      setA(data.a);
      setGotResponse(true);
    });
  };

  return (
    <div>
      <div>Hello world in a component.</div>
      <div>You clicked {count} times.</div>
      <button onClick={() => setCount(count + 1)}>Click me.</button>
      <button onClick={() => sendRequest()}>Send API request.</button>
      { gotResponse && (
        <div>
          The server told us the answer.  It's {a}!
        </div>
      )}
    </div>
  );
}

ReactDOM.render(<Home />, document.querySelector("[home-app]"));