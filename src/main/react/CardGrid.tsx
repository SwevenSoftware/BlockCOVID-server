import React, { Component, createRef, RefObject } from "react";
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles';
import Card from "@material-ui/core/Card";
import CardActions from "@material-ui/core/CardActions";
import CardContent from "@material-ui/core/CardContent";
import Button from "@material-ui/core/Button";
import { ThemeProvider } from "@material-ui/styles";
import { createMuiTheme } from "@material-ui/core/styles";
import "./styles.css";
import { green, red } from "@material-ui/core/colors";
import DotGrid from "./DotGrid";

import GeneralLayout from './GeneralLayout'

const theme = createMuiTheme({
  palette: {
    //type: "dark",
    secondary: {
      main: red[500]
    },
    primary: {
      main: green[700]
    }
  },
  typography: {
    // Use the system font instead of the default Roboto font.
    fontFamily: ['"Lato"', "sans-serif"].join(",")
  }
});

interface StateApp {
  width: number | null;
  height: number | null;
}

class CardGridApp extends Component<{}, StateApp, any> {
  dispGrid: RefObject<HTMLDivElement>;
  dotGrid: RefObject<DotGrid>;
  setGrid: StateApp;
  constructor(props) {
    super(props);
    this.dispGrid = createRef<HTMLDivElement>();
    this.dotGrid = createRef<DotGrid>();
    this.setGrid = {
      width: null,
      height: null
    };
  }

  /*updateStateGrid(d: HTMLDivElement | null) {
    if (d !== null) {
      console.log(d.clientWidth, d.clientHeight);
      this.setState({
        width: d.clientWidth,
        height: d.clientHeight
      });
    }
  }*/

  componentDidMount() {
    if (this.dispGrid.current && this.dotGrid.current) {
      this.dotGrid.current.setSize(
        this.dispGrid.current.clientWidth,
        this.dispGrid.current.clientHeight
      );
    }
  }

  render() {
    return (
      <div>
        <ThemeProvider theme={theme}>
          <Card className="cardGrid">
            <CardContent ref={this.dispGrid} className="dispGrid">
              <DotGrid ref={this.dotGrid} width={this.setGrid.width || 0} />
            </CardContent>
            <CardActions>
              <Button size="medium" color="primary">
                Salva stanza
              </Button>
              <Button size="medium" color="secondary">
                Annulla
              </Button>
            </CardActions>
          </Card>
        </ThemeProvider>
      </div>
    );
  }
}

const CardGrid = () => {
  return (
    GeneralLayout(<CardGridApp />)
  );
}

export default CardGrid;
