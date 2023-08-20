import React from 'react';
import ReactDOM from 'react-dom/client';
import './styles.css';
import 'bootstrap/dist/css/bootstrap.min.css';

import { RouterProvider } from 'react-router';

import router from './navigation/RouterConfig'

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>
);