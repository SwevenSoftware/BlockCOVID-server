import React, { Component, createRef, RefObject } from "react";
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles';
import Card from "@material-ui/core/Card";
import CardActions from "@material-ui/core/CardActions";
import CardContent from "@material-ui/core/CardContent";
import CardHeader from "@material-ui/core/CardHeader";
import Button from "@material-ui/core/Button";
import { ThemeProvider } from "@material-ui/styles";
import { createMuiTheme } from "@material-ui/core/styles";
import "./styles.css";
import { green, red } from "@material-ui/core/colors";
import DotGrid from "./DotGrid";

import GeneralLayout from './GeneralLayout'
import Token from './Token'

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
    this.resetGrid = this.resetGrid.bind(this)
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

  private resetGrid() {
    this.dotGrid.current?.resetView();
  }

  render() {
    return (
      <div>
        <ThemeProvider theme={theme}>
          <Card className="cardGrid">
            <CardHeader className="headerCard" title="New room"/>
            <CardContent ref={this.dispGrid} className="dispGrid">
              <DotGrid ref={this.dotGrid} width={this.setGrid.width || 0} />
            </CardContent>
            <CardActions>
              <Button size="medium" color="primary">
                Save room
              </Button>
              <Button size="medium" color="secondary" onClick={this.resetGrid}>
                Reset
              </Button>
            </CardActions>
          </Card>
        </ThemeProvider>
      </div>
    );
  }
}

const CardGrid = () => {

  if(!Token.get())
    location.href = "/login"

  return (
    GeneralLayout(<CardGridApp />)
  );
}

export default CardGrid;
