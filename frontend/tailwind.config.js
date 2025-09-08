/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './index.html',
    './src/**/*.{ts,tsx}',
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#6C5CE7',
          50: '#F2F1FE',
          100: '#E6E4FD',
          200: '#C9C4FB',
          300: '#ACA4F9',
          400: '#8F84F7',
          500: '#6C5CE7',
          600: '#564AB9',
          700: '#40398B',
          800: '#2B275D',
          900: '#15142F',
        },
        accent: '#00D8A4',
      },
      borderRadius: {
        xl: '1rem',
      },
    },
  },
  plugins: [],
}

