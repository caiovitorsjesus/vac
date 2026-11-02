import { Route, Routes } from 'react-router'
import { Layout } from './components/Layout'

export function App() {
  return (
    <Layout>
      <Routes>
        <Route
          path="/"
          element={
            <div>
              <h1 className="text-3xl font-extrabold tracking-tight md:text-4xl">Seu semestre em dia.</h1>
              <p className="mt-1 text-muted-foreground">Mais clareza nas faltas. Mais tranquilidade nas notas.</p>
            </div>
          }
        />
      </Routes>
    </Layout>
  )
}
