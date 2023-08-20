import Header from './components/Header'
import Footer from './components/Footer';
import { Container, Button, Row, Col } from 'react-bootstrap';

function App() {
  return (
    <>
      <Header></Header>
      <Container>
        <Col xs={2}>
          <Row className='mb-3'>
            <Button href="/user" size='sm'>User reimbursement form</Button>
          </Row>
          <Row className='mb-3'>
            <Button href="/admin" size='sm'>Admin limits form</Button>
          </Row>
        </Col>
      </Container >
      <Footer></Footer>
    </>
  );
}

export default App;
