module.exports = {
    parser: "vue-eslint-parser",
    parserOptions: {
        parser: "@babel/eslint-parser", // JavaScript-Parser
        ecmaVersion: 2020,
        sourceType: "module"
    },
    env: {
        browser: true, // Damit Browser-spezifische Variablen wie "window" erlaubt sind
        node: true     // Damit Node.js-Variablen wie "process" erlaubt sind
    },
    extends: [
        "eslint:recommended",
        "plugin:vue/recommended" // Vue-spezifische Regeln
    ],
    rules: {
        "no-control-regex": "off"
    }
};