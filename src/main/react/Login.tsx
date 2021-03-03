import React, { useState, useEffect } from 'react';
import { createStyles, makeStyles, Theme} from '@material-ui/core/styles';

import TextField from '@material-ui/core/TextField';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import CardActions from '@material-ui/core/CardActions';
import CardHeader from '@material-ui/core/CardHeader';
import Button from '@material-ui/core/Button';
import axios from 'axios';

import GeneralLayout from './GeneralLayout';
import Token from './Token';

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

const LoginForm = () => {
  const classes = useStyles();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [isButtonDisabled, setIsButtonDisabled] = useState(true);
  const [helpText, setHelpText] = useState('');
  const [isError, setIsError] = useState(false);

  const cardTitle = "Login";
  const loginBtnText = "Login";

  useEffect(() => {
    if (username.trim() && password.trim()) {
      setIsButtonDisabled(false)
    } else {
      setIsButtonDisabled(true)
    }
  }, [username, password]);

  const successLogin = () => {
    setHelpText('');
    setIsError(false);
  }

  const failLogin = (message : string) => {
    setHelpText(message);
    setIsError(true);
    console.log("Fail error: ", isError)
  }

  const handleLogin = () => {
    console.log(JSON.stringify({
      username,
      password
    }));
    const formData = new FormData();
    formData.append('username', username);
    formData.append('password', password);

    const config = {headers: { 'content-type': 'multipart/form-data'}};

    axios.post("/api/login", formData, config)
    .then((res) => {
      successLogin();
      Token.set(res.data);
      location.href = "/home";
    }).catch((err) => {
      if(err.response.status == 401) {
        failLogin('Incorrect username or password')
      } else {
        failLogin('Server error')
      }
    });
  };

  const handleKeyPress = (event: React.KeyboardEvent) => {
    if (event.key == "Enter") {
      isButtonDisabled || handleLogin();
    }
  };

  const handleUsernameChange: React.ChangeEventHandler<HTMLInputElement> =
    (event) => {
      setUsername(event.target.value);
    };

  const handlePasswordChange: React.ChangeEventHandler<HTMLInputElement> =
    (event) => {
      setPassword(event.target.value);
    }
  return (
    <form className={classes.container} noValidate autoComplete="off">
      <Card className={classes.card}>
        <CardHeader className={classes.header} title={cardTitle} />
        <CardContent>
          <div>
            <TextField
              error={isError}
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
              error={isError}
              fullWidth
              id="password"
              type="password"
              label="Password"
              placeholder="Password"
              margin="normal"
              helperText={helpText}
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
              disabled={isButtonDisabled}>
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