import React, { useReducer, useEffect } from 'react';
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles';

import TextField from '@material-ui/core/TextField';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import CardActions from '@material-ui/core/CardActions';
import CardHeader from '@material-ui/core/CardHeader';
import Button from '@material-ui/core/Button';
import { Link } from 'react-router-dom';
import { FormHelperText } from '@material-ui/core';
import axios from 'axios'

import GeneralLayout from './GeneralLayout'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    container: {
      display: 'flex',
      flexWrap: 'wrap',
      width: 400,
      margin: `${theme.spacing(0)} auto`
    },
    loginBtn: {
      marginTop: theme.spacing(2),
      flexGrow: 1,
      background: "#319e77",

      "&:hover" : {
        background: "#31729e"
      }
    },
    header: {
      textAlign: 'center',
      background: '#319e77',
      color: '#fff'
    },
    card: {
      marginTop: theme.spacing(10)
    }
  })
);

//state type

type State = {
  username: string
  password:  string
  isButtonDisabled: boolean
  helperText: string
  isError: boolean
};

const initialState:State = {
  username: '',
  password: '',
  isButtonDisabled: true,
  helperText: '',
  isError: false
};

type Action = { type: 'setUsername', payload: string }
  | { type: 'setPassword', payload: string }
  | { type: 'setIsButtonDisabled', payload: boolean }
  | { type: 'loginSuccess', payload: string }
  | { type: 'loginFailed', payload: string }
  | { type: 'setIsError', payload: boolean };

const reducer = (state: State, action: Action): State => {
  switch (action.type) {
    case 'setUsername':
      return {
        ...state,
        username: action.payload
      };
    case 'setPassword':
      return {
        ...state,
        password: action.payload
      };
    case 'setIsButtonDisabled':
      return {
        ...state,
        isButtonDisabled: action.payload
      };
    case 'loginSuccess':
      return {
        ...state,
        helperText: action.payload,
        isError: false
      };
    case 'loginFailed':
      return {
        ...state,
        helperText: action.payload,
        isError: true
      };
    case 'setIsError':
      return {
        ...state,
        isError: action.payload
      };
  }
}



const LoginForm = () => {
  const classes = useStyles();
  const [state, dispatch] = useReducer(reducer, initialState);
  const cardTitle = "Login";
  const loginBtnText = "Login";

  useEffect(() => {
    if (state.username.trim() && state.password.trim()) {
     dispatch({
       type: 'setIsButtonDisabled',
       payload: false
     });
    } else {
      dispatch({
        type: 'setIsButtonDisabled',
        payload: true
      });
    }
  }, [state.username, state.password]);

  const successLogin = (message : string) => {
    dispatch({
      type: 'loginSuccess',
      payload: message,
    });
  }

  const failLogin = (message : string) => {
    dispatch({
      type: 'loginFailed',
      payload: message,
    });
  }

  const handleLogin = () => {
    console.log(JSON.stringify({
      username: state.username,
      password: state.password
    }));
    const formData = new FormData();
    formData.append('username', state.username);
    formData.append('password', state.password);
    /*const options = {
        method: 'POST',
        body: formData
      }*/

    const config = {headers: { 'content-type': 'multipart/form-data'}};

    axios.post("http://localhost:8080/api/login", formData, config).then((res) => {console.log(res)});

    /*fetch("http://localhost:8080/api/login",
        options
    ).then(
      (result) => {
        console.log(result);
        switch (result.status) {
          case 200:
            successLogin("Login successfully");
            break;
          case 401:
            failLogin("Incorrect username or password");
            break;
        }
      },
      () => {
        failLogin('An error occured while processing the request');
      }
    )*/
  };

  const handleKeyPress = (event: React.KeyboardEvent) => {
    if (event.key == "Enter") {
      state.isButtonDisabled || handleLogin();
    }
  };

  const handleUsernameChange: React.ChangeEventHandler<HTMLInputElement> =
    (event) => {
      dispatch({
        type: 'setUsername',
        payload: event.target.value
      });
    };

  const handlePasswordChange: React.ChangeEventHandler<HTMLInputElement> =
    (event) => {
      dispatch({
        type: 'setPassword',
        payload: event.target.value
      });
    }
  return (
    <form className={classes.container} noValidate autoComplete="off">
      <Card className={classes.card}>
        <CardHeader className={classes.header} title={cardTitle} />
        <CardContent>
          <div>
            <TextField
              error={state.isError}
              fullWidth
              id="username"
              type="email"
              label="Username"
              placeholder="Username"
              margin="normal"
              onChange={handleUsernameChange}
              onKeyPress={handleKeyPress}
            />
            <TextField
              error={state.isError}
              fullWidth
              id="password"
              type="password"
              label="Password"
              placeholder="Password"
              margin="normal"
              helperText={state.helperText}
              onChange={handlePasswordChange}
              onKeyPress={handleKeyPress}
            />
          </div>
        </CardContent>
        <CardActions>
          <Button
              variant="contained"
              size="large"
              color="secondary"
              className={classes.loginBtn}
              onClick={handleLogin}
              disabled={state.isButtonDisabled}>
              {loginBtnText}
          </Button>
        </CardActions>
      </Card>
    </form>
  );
}

const Login = () => {
  return (
    GeneralLayout(<LoginForm />)
  );
}

export default Login