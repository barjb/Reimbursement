import App from "../App"
import Admin from '../pages/Admin/Admin';
import User from '../pages/User/User';
import { createBrowserRouter } from 'react-router-dom';

const router = createBrowserRouter([
    {
        path: '/',
        element: <App />
    },
    {
        path: '/user',
        element: <User />
    },
    {
        path: '/admin',
        element: <Admin />
    }
]);

export default router;