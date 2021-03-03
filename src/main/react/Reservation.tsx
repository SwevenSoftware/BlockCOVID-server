import React from 'react';
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';

import GeneralLayout from './GeneralLayout'

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

function createData(ID: string, IDdesk: number, room: number, date: string, from: string, to: string, name: string,) {
  return { ID, IDdesk, room, date, from, to, name };
}

const rows = [
  createData('1111', 1, 1, '1/1/2021', '08:00', '10:00', 'Luca M.'),
  createData('2222', 2, 1, '1/1/2021', '08:00', '12:00', 'Alberto'),
  createData('3333', 3, 2, '1/1/2021', '08:00', '10:00', 'Luca Z.'),
  createData('4444', 4, 2, '1/1/2021', '08:00', '14:00', 'Michele'),
  createData('5555', 5, 3, '1/1/2021', '08:00', '10:00', 'Francesco'),
  createData('6666', 6, 3, '1/1/2021', '08:00', '14:00', 'Antonio'),
  createData('7777', 7, 2, '1/1/2021', '08:00', '10:00', 'Dardan'),
];

const ReservationsForm = () => {
  const classes = useStyles();

  return (
    <div className={classes.container}>
        <TableContainer component={Paper}>
      <Table className={classes.table} aria-label="simple table">
        <TableHead className={classes.header}>
          <TableRow>
            <TableCell className={classes.headerText}>Reservation ID</TableCell>
            <TableCell align="right" className={classes.headerText}>Desk</TableCell>
            <TableCell align="right" className={classes.headerText}>Room</TableCell>
            <TableCell align="right" className={classes.headerText}>Date</TableCell>
            <TableCell align="right" className={classes.headerText}>From</TableCell>
            <TableCell align="right" className={classes.headerText}>To</TableCell>
            <TableCell align="right" className={classes.headerText}>Username</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((row) => (
            <TableRow key={row.ID}>
              <TableCell component="th" scope="row">
                {row.ID}
              </TableCell>
              <TableCell align="right">{row.IDdesk}</TableCell>
              <TableCell align="right">{row.room}</TableCell>
              <TableCell align="right">{row.date}</TableCell>
              <TableCell align="right">{row.from}</TableCell>
              <TableCell align="right">{row.to}</TableCell>
              <TableCell align="right">{row.name}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
    </div>
  );
}

const Reservations = () => {
  return (
    GeneralLayout(<ReservationsForm />)
  );
}

export default Reservations;