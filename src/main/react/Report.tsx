import React from 'react'
import { Button } from "@material-ui/core"
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles';
import {saveAs} from 'file-saver'
import Token from './Token'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    button: {
        margin: `1em auto 1em auto`
    }
  })
);

const Report = () => {
    const classes = useStyles();

    const generateReport = () => {
        const token : string | undefined = Token.get()?.toString()
        if(token)
            fetch('/api/admin/report', {
                method: 'POST',
                headers: { 'Authorization': token }
            }).then((res) => {
                if (res.ok) { 
                    return res.blob().then(blob => {
                        const name = 'Report.pdf';
                        saveAs(blob, name);
                    });
                }
            })
    }

    return (
        <Button 
            className={classes.button} 
            variant="contained" 
            onClick={generateReport}>
        Generate Report
        </Button>
    )
}

export default Report;