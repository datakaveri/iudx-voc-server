// const CompressionPlugin = require('compression-webpack-plugin');

// module.exports = {
//     plugins: [new CompressionPlugin()],
// };

const webpack = require('webpack');

module.exports = {
    plugins: [
        new webpack.DefinePlugin({
            'STABLE_FEATURE': JSON.stringify(true),
            'EXPERIMENTAL_FEATURE': JSON.stringify(false)
        })
    ]
};