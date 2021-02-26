import React from 'react';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import './styles.css';
import Login from './Login';


const App: React.FC = () => {

  return (
      <div>
        <BrowserRouter>
          <Switch>
            <Route path='/login' exact component={Login}/>
            <Route path='/' render= {() => <div>Error 404</div>}/>
          </Switch>
        </BrowserRouter>
      </div>
  );
}

export default App;