import { Route, Routes } from 'react-router'
import { Layout } from './components/Layout'
import { Painel } from './pages/Painel'

export function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Painel />} />
      </Routes>
    </Layout>
  )
}
