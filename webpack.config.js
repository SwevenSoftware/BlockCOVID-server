var path = require('path');

module.exports = {
    entry: './src/main/react/index.js',
    devtool: 'inline-source-map',

    /*watch: true,
    watchOptions: {
        aggregateTimeout: 600,
        poll: 1000,
    },*/

    cache: true,
    mode: 'development',

    module: {
        rules: [
            {
                test: path.join(__dirname, './src/main/react'),
                exclude: /(node_modules)/,
                use: [{
                    loader: 'babel-loader',
                    options: {
                        presets: ["@babel/preset-env", "@babel/preset-react"]
                    }
                }]
            },{
                test: /\.tsx?$/,
                use: 'ts-loader',
                exclude: /node_modules/
            },
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader'],
                exclude: /node_modules/
            }

        ],
    }, resolve: {
        extensions: [ '.tsx', '.ts', '.js', '.css' ],
      },
      output: {
        path: __dirname,
        filename: './src/main/resources/static/built/bundle.js'
    },
};