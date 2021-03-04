import React, { RefObject, useEffect, createRef, ReactNode, Component } from 'react';
import ReactDOM from 'react-dom'
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

interface Reservation {
  id : number,
  room : string,
  desk : number,
  date : string,
  from : string,
  to : string,
  user : string
}

class ReservationsForm extends Component {
  rows : Array<JSX.Element> ;

  constructor(props) {
    super(props)
    this.rows = new Array()
  }

  private addTableRow(row : Reservation) {
    this.rows.push(
      <TableRow key={row.id}>
          <TableCell component="th" scope="row" align="left">
            {row.id}
          </TableCell>
          <TableCell align="center">{row.room}</TableCell>
          <TableCell align="center">{row.desk}</TableCell>
          <TableCell align="center">{row.date}</TableCell>
          <TableCell align="center">{row.from}</TableCell>
          <TableCell align="center">{row.to}</TableCell>
          <TableCell align="center">{row.user}</TableCell>
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
            <TableCell align="left">Reservation ID</TableCell>
            <TableCell align="center">Room</TableCell>
            <TableCell align="center">Desk</TableCell>
            <TableCell align="center">Date</TableCell>
            <TableCell align="center">From</TableCell>
            <TableCell align="center">To</TableCell>
            <TableCell align="center">Username</TableCell>
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

  if(!Token.get())
    location.href = "/login"

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