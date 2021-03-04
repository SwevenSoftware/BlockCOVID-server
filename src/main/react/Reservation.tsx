import React, { RefObject, useEffect, createRef, ReactNode, Component } from 'react';
import ReactDOM from 'react-dom'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';
import axios from 'axios'

import './styles.css'
import GeneralLayout from './GeneralLayout'
import Report from './Report'
import Token from './Token'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    container: {
      margin: `${theme.spacing(0)} auto`
    },
    header: {
      textAlign: 'center',
      background: '#319e77',
      color: '#fff'
    },
    headerText:{
      color: 'white',
      fontWeight: 'bold'
    },
    table: {
      minWidth: 650,
    }
  })
);

/*function createData(ID: string, IDdesk: number, room: number, date: string, from: string, to: string, name: string,) {
  return { ID, IDdesk, room, date, from, to, name };
}*/

/*const rows = [
  createData('1111', 1, 1, '1/1/2021', '08:00', '10:00', 'Luca M.'),
  createData('2222', 2, 1, '1/1/2021', '08:00', '12:00', 'Alberto'),
  createData('3333', 3, 2, '1/1/2021', '08:00', '10:00', 'Luca Z.'),
  createData('4444', 4, 2, '1/1/2021', '08:00', '14:00', 'Michele'),
  createData('5555', 5, 3, '1/1/2021', '08:00', '10:00', 'Francesco'),
  createData('6666', 6, 3, '1/1/2021', '08:00', '14:00', 'Antonio'),
  createData('7777', 7, 2, '1/1/2021', '08:00', '10:00', 'Dardan'),
];*/

interface Reservation {
  id : number,
  room : string,
  desk : number,
  date : string,
  from : string,
  to : string,
  user : string
}

class RowTable extends Node {
  row : Reservation;
  constructor(row : Reservation) {
    super()
    this.row = row
  }
  render() {
    return (
      <TableRow key={this.row.id}>
        <TableCell component="th" scope="row">
          {this.row.id}
        </TableCell>
        <TableCell align="right">{this.row.room}</TableCell>
        <TableCell align="right">{this.row.desk}</TableCell>
        <TableCell align="right">{this.row.date}</TableCell>
        <TableCell align="right">{this.row.from}</TableCell>
        <TableCell align="right">{this.row.to}</TableCell>
        <TableCell align="right">{this.row.user}</TableCell>
      </TableRow>
    )
  }
  
}

class ReservationsForm extends Component {
  //const classes = useStyles();
  //let reservations : Array<Reservation> = new Array();
  //let refTableBody : RefObject<HTMLTableSectionElement> = createRef<HTMLTableSectionElement>();
  rows : Array<JSX.Element> ;

  constructor(props) {
    super(props)
    this.rows = new Array()
  }

  private addTableRow(row : Reservation) {
    /* if(refTableBody.current)
    ReactDOM.render(
        {refTableBody.current.children}
        <TableRow key={row.id}>
          <TableCell component="th" scope="row">
            {row.id}
          </TableCell>
          <TableCell align="right">{row.room}</TableCell>
          <TableCell align="right">{row.desk}</TableCell>
          <TableCell align="right">{row.date}</TableCell>
          <TableCell align="right">{row.from}</TableCell>
          <TableCell align="right">{row.to}</TableCell>
          <TableCell align="right">{row.user}</TableCell>
        </TableRow>, refTableBody.current
    ) */
    /* const r : RowTable = new RowTable(row)
    refTableBody.current?.appendChild(r) */
    this.rows.push(
      <TableRow key={row.id}>
          <TableCell component="th" scope="row">
            {row.id}
          </TableCell>
          <TableCell align="right">{row.room}</TableCell>
          <TableCell align="right">{row.desk}</TableCell>
          <TableCell align="right">{row.date}</TableCell>
          <TableCell align="right">{row.from}</TableCell>
          <TableCell align="right">{row.to}</TableCell>
          <TableCell align="right">{row.user}</TableCell>
        </TableRow>
    )
  }

  private retrieveReservations() {
    const config = {headers: {"Authorization": Token.get()}};
    axios.post("/api/user/reservations", {}, config).then((res) => {
      for(var id in res.data) {
        const data = res.data[id]
        const newReservation = {
          id: data.id, 
          room: data.nameRoom,
          desk: data.idDesk,
          date: data.date,
          from: data.from,
          to: data.to,
          user: data.user
        };
        this.addTableRow(newReservation);
        console.log(newReservation)
        //reservations.push(newReservation);
      }
      this.forceUpdate()
    })
  }

  componentDidMount() {
    this.retrieveReservations()
  }

  render() {
  return (
    <div>
      <TableContainer component={Paper}>
      <Table aria-label="simple table">
        <TableHead className="headerCard">
          <TableRow>
            <TableCell>Reservation ID</TableCell>
            <TableCell align="right">Room</TableCell>
            <TableCell align="right">Desk</TableCell>
            <TableCell align="right">Date</TableCell>
            <TableCell align="right">From</TableCell>
            <TableCell align="right">To</TableCell>
            <TableCell align="right">Username</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>{this.rows}
        </TableBody>
      </Table>
    </TableContainer>
    </div>
  )};
}

const Reservations = () => {
  return (
    GeneralLayout(
      <div>
        <ReservationsForm />
        <Report />
      </div>
    )
  );
}

export default Reservations;