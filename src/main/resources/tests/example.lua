local describe, test, expect = Test.describe, Test.it, Test.expect

describe("example", function()
    describe("module1", function()
        test("feature1", function()
            expect.equal('something', 'something')
        end)
        test("feature2", function()
            expect.truthy(false)
        end)
    end)
end)