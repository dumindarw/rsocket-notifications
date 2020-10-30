const HtmlWebPackPlugin = require( 'html-webpack-plugin' );
const path = require( 'path' );
const { Template } = require('webpack');

module.exports = {
    entry: './index.js',
    output:{
        path: path.resolve(__dirname, 'dist'),
        filename: 'main.js'
    },
    plugins: [
        new HtmlWebPackPlugin({
            template: './index.html',
            filename:  "./index.html"
        })
     ],
    module:{
        rules: [
            {
                test: /\.js$/,
                exclude: /node_modules/,
                use: {
                  loader: "babel-loader"
                }
              }
        ]
    },
    devServer: {
        contentBase: path.join(__dirname, 'dist'),
        compress: true,
        port: 9000
      }
}