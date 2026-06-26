import eslintJs from '@eslint/js';
import stylistic from '@stylistic/eslint-plugin';
import { defineConfig } from 'eslint/config';
// @ts-ignore
import jasmine from 'eslint-plugin-jasmine';
import jsdoc from 'eslint-plugin-jsdoc';
import sonarjs, { configs as sonarJsConfigs } from 'eslint-plugin-sonarjs';

export default defineConfig(
	{
		ignores: [
			'build',
			'coverage',
			'dist',
			'doc',
			'node_modules',
			'reports',
			'target'
		]
	},
	{
		languageOptions: { ecmaVersion: 2022 },
		linterOptions: { reportUnusedDisableDirectives: 'error' },
		plugins: { '@stylistic': stylistic, jasmine: jasmine, jsdoc: jsdoc, sonarjs: sonarjs }
	},
	eslintJs.configs.recommended,
	stylistic.configs.recommended,
	jasmine.configs.recommended,
	jsdoc.configs['flat/recommended-error'],
	{
		files: ['**/*.js', '**/*.cjs', '**/*.mjs', '**/*.cjsx', '**/*.mjsx'],
		rules: {
			...sonarJsConfigs.recommended.rules,

			'@stylistic/arrow-parens': 'off',
			'@stylistic/arrow-spacing': ['error', { after: true, before: true }],
			'@stylistic/brace-style': ['error', '1tbs', { allowSingleLine: true }],
			'@stylistic/comma-dangle': ['error', 'never'],
			'@stylistic/indent': ['error', 'tab', { SwitchCase: 1 }],
			'@stylistic/indent-binary-ops': 'off',
			'@stylistic/keyword-spacing': 'error',
			'@stylistic/linebreak-style': ['error', 'unix'],
			'@stylistic/no-extra-semi': 'off',
			'@stylistic/no-mixed-operators': ['error', { allowSamePrecedence: false }],
			'@stylistic/no-multi-spaces': 'off',
			'@stylistic/no-multiple-empty-lines': ['error', { max: 5 }],
			'@stylistic/no-tabs': 'off',
			'@stylistic/operator-linebreak': 'off',
			'@stylistic/object-curly-spacing': ['error', 'always'],
			'@stylistic/semi': ['error', 'always'],
			'@stylistic/semi-spacing': 'error',
			'@stylistic/space-before-blocks': 'error',
			'@stylistic/space-before-function-paren': 'off',
			'@stylistic/spaced-comment': 'error',
			'@stylistic/quote-props': 'off',
			'@stylistic/quotes': 'off',
			'jsdoc/check-tag-names': 'off',
			'jsdoc/no-undefined-types': 'off',
			'jsdoc/reject-any-type': 'warn',
			'jsdoc/reject-function-type': 'off',
			'jsdoc/require-jsdoc': 'off',
			'jsdoc/require-returns': 'off',
			'jsdoc/valid-types': 'off',
			'sonarjs/deprecation': 'error',
			'sonarjs/no-empty-test-file': 'off',
			'no-console': 'error',
			'no-extra-semi': 'off',
			'no-prototype-builtins': 'off',
			'no-shadow': 'error',
			quotes: 'off'
		}
	}
);
