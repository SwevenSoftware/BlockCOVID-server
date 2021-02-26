import { StrictMode } from "react";
import ReactDOM from "react-dom";
import React from 'react';

import App from "./App";

const rootElement = document.getElementById("react");
ReactDOM.render(
  <StrictMode>
    <App />
  </StrictMode>,
  rootElement
);