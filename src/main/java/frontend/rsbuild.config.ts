import { defineConfig } from "@rsbuild/core";
import { pluginReact } from "@rsbuild/plugin-react";
import { pluginBabel } from "@rsbuild/plugin-babel";
import { pluginTypeCheck } from "@rsbuild/plugin-type-check";

const isDev = process.env.NODE_ENV === "development";
const isProd = process.env.NODE_ENV === "production";

export default defineConfig({
  html: {
    template: "./index.html",
    title: "Byransha",
    meta: {
      charset: "utf-8",
      viewport: "width=device-width, initial-scale=1.0",
      description: "Byransha",
    },
  },
  source: {
    entry: {
      index: "./src/index.tsx",
    },
    define: {
      __DEV__: isDev,
      __REACT_19_FEATURES__: true,
    },
  },
  resolve: {
    alias: {
      "@": "./src",
      "@components": "./src/Components",
      "@global": "./src/global",
      "@hooks": "./src/hooks",
      "@utils": "./src/utils",
      "@config": "./src/config",
      "@features": "./src/features",
      "@shared": "./src/shared",
      "@common": "./src/Components/Common",
    },
  },
  output: {
    cleanDistPath: !isDev,
    distPath: {
      root: "../../../../build/frontend",
    },
    filename: {
      js: isProd ? "[name].[contenthash:8].js" : "[name].js",
      css: isProd ? "[name].[contenthash:8].css" : "[name].css",
    },
    target: "web",
    sourceMap: {
      js: isDev ? "eval-cheap-module-source-map" : "source-map",
      css: true,
    },
  },
  server: {
    port: 5173,
    host: "localhost",
    open: false,
    historyApiFallback: true,
    headers: {
      "Access-Control-Allow-Origin": "*",
    },
  },
  performance: {
    removeConsole: isProd ? ["log", "warn"] : false,
    preload: isProd ? true : undefined,
    prefetch: isProd ? true : undefined,
    chunkSplit: {
      strategy: "split-by-experience",
    },
  },
  plugins: [
    pluginTypeCheck(),
    pluginReact({
      reactRefreshOptions: {
        overlay: isDev,
      },
      ...(isDev && {
        devtool: {
          reactDevtools: true,
        },
      }),
    }),
    pluginBabel({
      include: /\.(?:jsx|tsx)$/,
      babelLoaderOptions(opts) {
        opts.plugins?.unshift([
          "babel-plugin-react-compiler",
          {
            runtimeModule: "react/compiler-runtime",
            sources: (filename: string) => {
              return (
                filename.includes("src/") &&
                (filename.endsWith(".tsx") || filename.endsWith(".jsx"))
              );
            },
            enableReactCompilerOptimizations: true,
            ...(isDev && {
              logger: {
                logLevel: "info",
                logFilename: "react-compiler.log",
              },
            }),
          },
        ]);

        if (isProd) {
          opts.plugins?.push([
            "transform-react-remove-prop-types",
            {
              mode: "remove",
              removeImport: true,
              additionalLibraries: ["@mui/material"],
            },
          ]);
        }

        opts.presets = [
          [
            "@babel/preset-env",
            {
              modules: false,
              targets: {
                browsers: ["> 1%", "last 2 versions", "not ie <= 11"],
              },
              useBuiltIns: "entry",
              corejs: 3,
            },
          ],
          [
            "@babel/preset-react",
            {
              runtime: "automatic",
              importSource: "react",
              development: isDev,
            },
          ],
          [
            "@babel/preset-typescript",
            {
              isTSX: true,
              allExtensions: true,
            },
          ],
        ];

        return opts;
      },
    }),
  ],
  dev: {
    progressBar: true,
    hmr: true,
    liveReload: true,
  },
});
