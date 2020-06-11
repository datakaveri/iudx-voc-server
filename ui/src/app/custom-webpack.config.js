const CompressionPlugin = require('compression-webpack-plugin');
const webpack = require('webpack');

module.exports = {
    plugins: [new CompressionPlugin(), new webpack.DefinePlugin({
        'STABLE_FEATURE': JSON.stringify(true),
        'EXPERIMENTAL_FEATURE': JSON.stringify(false)
    })]
};



