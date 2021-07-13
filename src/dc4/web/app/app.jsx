import { React, ReactDOM, useEffect, useState } from "react.mjs";
import DC4Websockets from "websockets.mjs";
import TestPage from "page/home/test-page.jsx";
import Game from "page/game/game.jsx";

function App() {
    const [page, setPage] = useState("test");
    const [gameId, setGameId] = useState(null);

    const startGame = (gameId) => {
        setPage("game");
        setGameId(gameId);
    }

    const websockets = new DC4Websockets();

    switch(page) {
        case "test": return <TestPage startGame={startGame} websockets={websockets}></TestPage>
        case "game": return <Game gameId={gameId}></Game>
        default: return null;
    }
}


ReactDOM.render(<App />, document.querySelector("[home-app]"));