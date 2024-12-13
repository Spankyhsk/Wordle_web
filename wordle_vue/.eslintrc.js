module.exports = {
    parser: "vue-eslint-parser",
    parserOptions: {
        parser: "@babel/eslint-parser", // JavaScript-Parser
        ecmaVersion: 2020,
        sourceType: "module"
    },
    extends: [
        "eslint:recommended",
        "plugin:vue/recommended" // Vue-spezifische Regeln
    ],
    rules: {
        "no-control-regex": "off"
    }
};