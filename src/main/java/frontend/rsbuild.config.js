import { defineConfig } from '@rsbuild/core';
import { pluginReact } from '@rsbuild/plugin-react';
import { pluginBabel } from '@rsbuild/plugin-babel';

const isDev = process.env.NODE_ENV === 'development';

export default defineConfig({
    html: {
        template: './index.html',
    },
    source: {
        entry: {
            index: './src/index.jsx'
        }
    },
    output: {
        cleanDistPath: !isDev,
        distPath: {
            root: '../../../../build/frontend'
        }
    },
    server: {
        port: 5173
    },
    performance: {
        removeConsole: isDev ? false : ['log', 'warn'],
        preload: true
    },
    plugins: [
        pluginReact(),
        pluginBabel({
            include: /\.(?:jsx|tsx)$/,
            babelLoaderOptions(opts) {
                opts.plugins?.unshift('babel-plugin-react-compiler');
            },
        }),
    ],
});