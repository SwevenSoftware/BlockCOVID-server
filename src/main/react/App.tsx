import React from 'react';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import { StylesProvider } from '@material-ui/styles';
import './styles.css';
import Login from './Login';
import Reservations from './Reservation';
import CardGrid from './CardGrid';


const App: React.FC = () => {

  return (
      <div>
        <BrowserRouter>
          <Switch>
            <Route path='/login' exact component={Login}/>
            <Route path='/reservations' exact component={Reservations}/>
            <Route path='/desk' exact component={CardGrid}/>
            <Route path='/' render= {() => <div>Error 404</div>}/>
          </Switch>
        </BrowserRouter>
      </div>
  );
}

export default App;